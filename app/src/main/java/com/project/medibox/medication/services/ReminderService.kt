package com.project.medibox.medication.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.project.medibox.R
import com.project.medibox.controllers.activities.MainActivity
import com.project.medibox.medication.models.Frequency
import com.project.medibox.medication.models.HistoricalReminder
import com.project.medibox.medication.models.Interval
import com.project.medibox.medication.models.Reminder
import com.project.medibox.medication.models.UpcomingReminderAlarm
import com.project.medibox.medication.network.MedicationApiService
import com.project.medibox.medication.persistence.UpcomingReminderAlarmDAO
import com.project.medibox.medication.receivers.ConfirmAlarmReceiver
import com.project.medibox.medication.receivers.MissAlarmReceiver
import com.project.medibox.medication.receivers.PostponeAlarmReceiver
import com.project.medibox.pillboxmanagement.models.BoxData
import com.project.medibox.pillboxmanagement.models.BoxDataResponse
import com.project.medibox.pillboxmanagement.network.PillboxApiService
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import kotlin.random.Random

class ReminderService : Service() {

    private val TAG = "ReminderService"
    private val handler = Handler()
    private lateinit var notificationManager: NotificationManager
    private lateinit var reminderNotification: Notification
    private var isStarted = false

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isStarted) {
            makeForeground()
            isStarted = true
            Log.d(TAG, "Service started.")
            serviceLogic()
        }
        return START_NOT_STICKY
    }
    private fun makeForeground() {
        createServiceNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Reminder Service")
            .setContentText("Service running...")
            .setSmallIcon(R.drawable.ic_stat_name)
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }
    private fun defineNotification(medicationName: CharSequence, notificationId: Int) {
        val flag = PendingIntent.FLAG_IMMUTABLE
        val postponeIntent = Intent(this, PostponeAlarmReceiver::class.java).apply {
            putExtra("notificationId", notificationId.toString())
        }
        val postponePendingIntent = PendingIntent.getBroadcast(this, notificationId, postponeIntent, flag)
        val confirmIntent = Intent(this, ConfirmAlarmReceiver::class.java).apply {
            putExtra("notificationId", notificationId.toString())
        }
        val confirmPendingIntent = PendingIntent.getBroadcast(this, notificationId, confirmIntent, flag)
        val missIntent = Intent(this, MissAlarmReceiver::class.java).apply {
            putExtra("notificationId", notificationId.toString())
        }
        val missPendingIntent = PendingIntent.getBroadcast(this, notificationId, missIntent, flag)
        reminderNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.time_for_medication_notif))
            .setContentText(medicationName)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentIntent(createPendingIntent(MainActivity::class.java))
            .setAutoCancel(true)
            .addAction(0, getString(R.string.confirm), confirmPendingIntent)
            .addAction(0, getString(R.string.forgotten), missPendingIntent)
            .addAction(0, getString(R.string.postpone), postponePendingIntent)
            .build()
    }
    private fun createServiceNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Reminder Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun serviceLogic() {
        handler.apply {
            val runnable = object : Runnable {
                override fun run() {
                    val now = LocalDateTime.now()
                    val dateString = SharedMethods.getDDMMYYStringFromDate(now)
                    val upcomingAlarmDAO = AppDatabase.getInstance(this@ReminderService).getUpcomingReminderAlarmDao()
                    val query = upcomingAlarmDAO.getAll()
                    val upcomingAlarm = query.find {
                        it.activateDateString == dateString &&
                                LocalTime.of(now.hour, now.minute) >= LocalTime.of(it.activateHour, it.activateMinute) &&
                                !it.notified
                    }
                    if (upcomingAlarm != null) {
                        if (!isNotificationVisible(upcomingAlarm.notificationId)) {
                            Log.d(TAG, "Sending reminder notification...")
                            defineNotification(upcomingAlarm.medicineName, upcomingAlarm.notificationId)
                            StateManager.selectedUpcomingAlarm = upcomingAlarm
                            notificationManager.notify(upcomingAlarm.notificationId, reminderNotification)
                            sendDataToPillbox()
                            upcomingAlarmDAO.setNotifiedById(upcomingAlarm.id)
                            Log.d(TAG, "Notification data: ${upcomingAlarm.activateDateString}, ${upcomingAlarm.activateHour}, ${upcomingAlarm.activateMinute}")
                        }
                        else Log.d(TAG, "Reminder notification is present.")
                    } else Log.d(TAG, "No reminder found.")
                    postDelayed(this, 1000)
                }
            }
            postDelayed(runnable, 1000)
        }
    }

    private fun sendDataToPillbox() {
        val toneSettings = AppDatabase.getInstance(this).getToneSettingsDao().getSettings()
        Log.d(TAG, "Tone Settings: $toneSettings")
        val pillboxService = SharedMethods.retrofitServiceBuilder(PillboxApiService::class.java)
        val request = pillboxService.updatePillboxData(StateManager.selectedPillboxId, BoxData(StateManager.selectedPillboxId, true, toneSettings.tone.toInt()))
        request.enqueue(object : Callback<BoxDataResponse> {
            override fun onResponse(p0: Call<BoxDataResponse>, p1: Response<BoxDataResponse>) {
                Log.d("ReminderService", "Sended info of reminder to your pillbox")
            }

            override fun onFailure(p0: Call<BoxDataResponse>, p1: Throwable) {

            }

        })
    }


    override fun onDestroy() {
        super.onDestroy()
        isStarted = false
        Log.d(TAG, "Service destroyed.")
        handler.removeCallbacksAndMessages(null)
    }

    private fun isNotificationVisible(notificationId: Int): Boolean {
        val activeNotifications = notificationManager.activeNotifications
        val foundNotification = activeNotifications.find { it.id == notificationId }

        return foundNotification != null
    }

    private fun <T> createPendingIntent(clsActivity: Class<T>): PendingIntent? {
        val intent = Intent(this, clsActivity)
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(clsActivity)
        stackBuilder.addNextIntent(intent)
        val pendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_IMMUTABLE)
        return pendingIntent
    }



    companion object {
        private const val ONGOING_NOTIFICATION_ID = 105
        private const val CHANNEL_ID = "1003"

        fun getUpcomingUniqueDateStrList(context: Context): List<String> {
            val upcomingAlarmDAO = AppDatabase.getInstance(context).getUpcomingReminderAlarmDao()
            val query = upcomingAlarmDAO.getAll()

            val dateList = query.flatMap { listOf(it.activateDateString) }

            // Obtiene los valores únicos
            val uniqueDates = dateList.distinct()

            return uniqueDates
        }

        private fun saveHistoricalReminder(context: Context, reminder: Reminder) {
            val createdDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.createdDateString)
            val createdDateParsed = SharedMethods.getDDMMYYStringFromDate(createdDate)
            val endDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.endDateString!!)
            val endDateParsed = SharedMethods.getDDMMYYStringFromDate(endDate)
            val type: String
            val typeId: Long
            if (reminder.interval != null) {
                type = "Interval"
                typeId = reminder.interval!!.id
            }
            else {
                type = "Frequency"
                typeId = reminder.frequency!!.id
            }
            AppDatabase.getInstance(context).getHistoricalReminderDao().insertReminder(HistoricalReminder(
                0,
                reminder.createdDateString,
                createdDateParsed,
                reminder.pills,
                reminder.endDateString!!,
                endDateParsed,
                reminder.medicine.name,
                type,
                typeId,
                reminder.consumeFood,
                reminder.id
            ))
        }

        fun loadAlarmsFromApi(context: Context) {
            val medicationApiService = SharedMethods.retrofitServiceBuilder(MedicationApiService::class.java)
            val request = medicationApiService.getRemindersByUserId(StateManager.authToken, StateManager.loggedUserId)
            val now = LocalDate.now()
            request.enqueue(object : Callback<List<Reminder>> {
                override fun onResponse(call: Call<List<Reminder>>, response: Response<List<Reminder>>) {
                    if (response.isSuccessful) {
                        response.body()!!.forEach {
                            saveHistoricalReminder(context, it)
                            val createdDate = SharedMethods.getLocalDateTimeFromJSDate(it.createdDateString)
                            val newDate = LocalDateTime.of(now, createdDate.toLocalTime())
                            it.createdDateString = SharedMethods.getJSDateFromLocalDateTime(newDate)
                            StateManager.selectedMedicine = it.medicine
                            if (it.interval != null)
                                createAlarms(context, it, it.interval!!)
                            else if (it.frequency != null)
                                createAlarms(context, it, it.frequency!!)
                        }
                    }
                }

                override fun onFailure(p0: Call<List<Reminder>>, p1: Throwable) {

                }

            })

        }

        fun startService(context: Context) {
            val intent = Intent(context, ReminderService::class.java)
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            AppDatabase.getInstance(context).getUpcomingReminderAlarmDao().clearTable()
            AppDatabase.getInstance(context).getHistoricalReminderDao().clearTable()
            AppDatabase.getInstance(context).getUpcomingReminderAlarmDao().clearTable()
            AppDatabase.getInstance(context).getCompletedReminderAlarmDao().clearTable()
            AppDatabase.getInstance(context).getMissedReminderAlarmDao().clearTable()
            val intent = Intent(context, ReminderService::class.java)
            context.stopService(intent)
        }

        private fun generateNotificationId(dao: UpcomingReminderAlarmDAO): Int {
            val existingIds = dao.getAll().map { it.notificationId }.toSet()
            val random = Random(System.nanoTime())
            while (true) {
                val notificationId = random.nextInt(Int.MAX_VALUE - 200 + 1) + 200
                if (notificationId !in existingIds) {
                    return notificationId
                }
            }
        }



        fun createAlarms(context: Context, reminder: Reminder, interval: Interval) {
            val endDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.endDateString!!)
            val createdDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.createdDateString)
            val dayDiff = Period.between(createdDate.toLocalDate(), endDate.toLocalDate()).days
            val upcomingReminderAlarmDAO = AppDatabase.getInstance(context).getUpcomingReminderAlarmDao()
            if (interval.intervalType == "Hours") {
                for (dayMore in 0L..dayDiff) {
                    val alarmDate = createdDate.plusDays(dayMore)
                    val alarmDateString = SharedMethods.getDDMMYYStringFromDate(alarmDate)
                    when(interval.intervalValue) {
                        6 -> {
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                StateManager.selectedMedicine!!.name,
                                alarmDateString,
                                8,
                                0,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id
                            ))
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                StateManager.selectedMedicine!!.name,
                                alarmDateString,
                                14,
                                0,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id
                            ))
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                StateManager.selectedMedicine!!.name,
                                alarmDateString,
                                20,
                                0,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id
                            ))
                        }
                        8 -> {
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                StateManager.selectedMedicine!!.name,
                                alarmDateString,
                                7,
                                0,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id
                            ))
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                StateManager.selectedMedicine!!.name,
                                alarmDateString,
                                15,
                                0,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id
                            ))
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                StateManager.selectedMedicine!!.name,
                                alarmDateString,
                                23,
                                0,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id
                            ))
                        }
                        12 -> {
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                StateManager.selectedMedicine!!.name,
                                alarmDateString,
                                createdDate.hour,
                                createdDate.minute,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id
                            ))
                            if (dayMore == 0L && createdDate.toLocalDate() == LocalDate.now()) {
                                if (createdDate.hour < 12) {
                                    upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                        0,
                                        StateManager.selectedMedicine!!.name,
                                        alarmDateString,
                                        createdDate.hour + 12,
                                        createdDate.minute,
                                        reminder.pills,
                                        reminder.consumeFood,
                                        generateNotificationId(upcomingReminderAlarmDAO),
                                        reminder.id
                                    ))
                                }
                            }
                            else {
                                val alarmHour = if (createdDate.hour < 12) createdDate.hour + 12 else createdDate.hour - 12
                                upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                    0,
                                    StateManager.selectedMedicine!!.name,
                                    alarmDateString,
                                    alarmHour,
                                    createdDate.minute,
                                    reminder.pills,
                                    reminder.consumeFood,
                                    generateNotificationId(upcomingReminderAlarmDAO),
                                    reminder.id
                                ))
                            }
                        }
                    }
                }

            }
            else {
                for (dayMore in 0L until dayDiff step interval.intervalValue.toLong()) {
                    val alarmDate = createdDate.plusDays(dayMore)
                    val alarmDateString = SharedMethods.getDDMMYYStringFromDate(alarmDate)
                    upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                        0,
                        StateManager.selectedMedicine!!.name,
                        alarmDateString,
                        createdDate.hour,
                        createdDate.minute,
                        reminder.pills,
                        reminder.consumeFood,
                        generateNotificationId(upcomingReminderAlarmDAO),
                        reminder.id
                    ))
                }
            }
        }
        fun createAlarms(context: Context, reminder: Reminder, interval: Interval, customMedicine: String, localReminderId: Short) {
            val endDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.endDateString!!)
            val createdDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.createdDateString)
            val dayDiff = Period.between(createdDate.toLocalDate(), endDate.toLocalDate()).days
            val upcomingReminderAlarmDAO = AppDatabase.getInstance(context).getUpcomingReminderAlarmDao()
            if (interval.intervalType == "Hours") {
                for (dayMore in 0L..dayDiff) {
                    val alarmDate = createdDate.plusDays(dayMore)
                    val alarmDateString = SharedMethods.getDDMMYYStringFromDate(alarmDate)
                    when(interval.intervalValue) {
                        6 -> {
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                customMedicine,
                                alarmDateString,
                                8,
                                0,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id,
                                localReminderId
                            ))
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                customMedicine,
                                alarmDateString,
                                14,
                                0,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id,
                                localReminderId
                            ))
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                customMedicine,
                                alarmDateString,
                                20,
                                0,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id,
                                localReminderId
                            ))
                        }
                        8 -> {
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                customMedicine,
                                alarmDateString,
                                7,
                                0,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id,
                                localReminderId
                            ))
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                customMedicine,
                                alarmDateString,
                                15,
                                0,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id,
                                localReminderId
                            ))
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                customMedicine,
                                alarmDateString,
                                23,
                                0,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id,
                                localReminderId
                            ))
                        }
                        12 -> {
                            upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                0,
                                customMedicine,
                                alarmDateString,
                                createdDate.hour,
                                createdDate.minute,
                                reminder.pills,
                                reminder.consumeFood,
                                generateNotificationId(upcomingReminderAlarmDAO),
                                reminder.id,
                                localReminderId
                            ))
                            if (dayMore == 0L && createdDate.toLocalDate() == LocalDate.now()) {
                                if (createdDate.hour < 12) {
                                    upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                        0,
                                        customMedicine,
                                        alarmDateString,
                                        createdDate.hour + 12,
                                        createdDate.minute,
                                        reminder.pills,
                                        reminder.consumeFood,
                                        generateNotificationId(upcomingReminderAlarmDAO),
                                        reminder.id,
                                        localReminderId
                                    ))
                                }
                            }
                            else {
                                val alarmHour = if (createdDate.hour < 12) createdDate.hour + 12 else createdDate.hour - 12
                                upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                                    0,
                                    customMedicine,
                                    alarmDateString,
                                    alarmHour,
                                    createdDate.minute,
                                    reminder.pills,
                                    reminder.consumeFood,
                                    generateNotificationId(upcomingReminderAlarmDAO),
                                    reminder.id,
                                    localReminderId
                                ))
                            }
                        }
                    }
                }

            }
            else {
                for (dayMore in 0L until dayDiff step interval.intervalValue.toLong()) {
                    val alarmDate = createdDate.plusDays(dayMore)
                    val alarmDateString = SharedMethods.getDDMMYYStringFromDate(alarmDate)
                    upcomingReminderAlarmDAO.insertAlarm(UpcomingReminderAlarm(
                        0,
                        StateManager.selectedMedicine!!.name,
                        alarmDateString,
                        createdDate.hour,
                        createdDate.minute,
                        reminder.pills,
                        reminder.consumeFood,
                        generateNotificationId(upcomingReminderAlarmDAO),
                        reminder.id,
                        localReminderId
                    ))
                }
            }
        }
        fun createAlarms(context: Context, reminder: Reminder, frequency: Frequency) {
            val endDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.endDateString!!)
            val createdDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.createdDateString)
            val dayDiff = Period.between(createdDate.toLocalDate(), endDate.toLocalDate()).days
            val upcomingReminderAlarmDAO = AppDatabase.getInstance(context).getUpcomingReminderAlarmDao()

            for (dayMore in 0L..dayDiff) {
                val alarmDate = createdDate.plusDays(dayMore)
                val alarmDateString = SharedMethods.getDDMMYYStringFromDate(alarmDate)
                when(frequency.times) {
                    1 -> {
                        val upc1 = UpcomingReminderAlarm(
                            0,
                            StateManager.selectedMedicine!!.name,
                            alarmDateString,
                            createdDate.hour,
                            createdDate.minute,
                            reminder.pills,
                            reminder.consumeFood,
                            generateNotificationId(upcomingReminderAlarmDAO),
                            reminder.id
                        )
                        upcomingReminderAlarmDAO.insertAlarm(upc1)
                        Log.d("Database", upc1.toString())
                    }
                    2 -> {
                        val upc1 = UpcomingReminderAlarm(
                            0,
                            StateManager.selectedMedicine!!.name,
                            alarmDateString,
                            8,
                            0,
                            reminder.pills,
                            reminder.consumeFood,
                            generateNotificationId(upcomingReminderAlarmDAO),
                            reminder.id
                        )
                        upcomingReminderAlarmDAO.insertAlarm(upc1)
                        Log.d("Database", upc1.toString())
                        val upc2 = UpcomingReminderAlarm(
                            0,
                            StateManager.selectedMedicine!!.name,
                            alarmDateString,
                            20,
                            0,
                            reminder.pills,
                            reminder.consumeFood,
                            generateNotificationId(upcomingReminderAlarmDAO),
                            reminder.id
                        )
                        upcomingReminderAlarmDAO.insertAlarm(upc2)
                        Log.d("Database", upc2.toString())
                    }
                }
            }
        }
        fun createAlarms(context: Context, reminder: Reminder, frequency: Frequency, customMedicine: String, localReminderId: Short) {
            val endDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.endDateString!!)
            val createdDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.createdDateString)
            val dayDiff = Period.between(createdDate.toLocalDate(), endDate.toLocalDate()).days
            val upcomingReminderAlarmDAO = AppDatabase.getInstance(context).getUpcomingReminderAlarmDao()

            for (dayMore in 0L..dayDiff) {
                val alarmDate = createdDate.plusDays(dayMore)
                val alarmDateString = SharedMethods.getDDMMYYStringFromDate(alarmDate)
                when(frequency.times) {
                    1 -> {
                        val upc1 = UpcomingReminderAlarm(
                            0,
                            customMedicine,
                            alarmDateString,
                            createdDate.hour,
                            createdDate.minute,
                            reminder.pills,
                            reminder.consumeFood,
                            generateNotificationId(upcomingReminderAlarmDAO),
                            reminder.id,
                            localReminderId
                        )
                        upcomingReminderAlarmDAO.insertAlarm(upc1)
                        Log.d("Database", upc1.toString())
                    }
                    2 -> {
                        val upc1 = UpcomingReminderAlarm(
                            0,
                            customMedicine,
                            alarmDateString,
                            8,
                            0,
                            reminder.pills,
                            reminder.consumeFood,
                            generateNotificationId(upcomingReminderAlarmDAO),
                            reminder.id,
                            localReminderId
                        )
                        upcomingReminderAlarmDAO.insertAlarm(upc1)
                        Log.d("Database", upc1.toString())
                        val upc2 = UpcomingReminderAlarm(
                            0,
                            customMedicine,
                            alarmDateString,
                            20,
                            0,
                            reminder.pills,
                            reminder.consumeFood,
                            generateNotificationId(upcomingReminderAlarmDAO),
                            reminder.id,
                            localReminderId
                        )
                        upcomingReminderAlarmDAO.insertAlarm(upc2)
                        Log.d("Database", upc2.toString())
                    }
                }
            }
        }
    }


}
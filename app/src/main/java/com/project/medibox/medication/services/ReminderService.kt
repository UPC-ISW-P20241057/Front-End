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
import com.project.medibox.medication.controller.activities.MedicationAlarmActivity
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.SharedMethods
import java.time.LocalDateTime

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
            .setSmallIcon(R.drawable.ic_test_notification)
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }
    private fun defineNotification(medicationName: CharSequence) {
        reminderNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Time for medication!")
            .setContentText(medicationName)
            .setSmallIcon(R.drawable.ic_test_notification)
            .setContentIntent(createPendingIntent(MedicationAlarmActivity::class.java))
            .setAutoCancel(true)
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
                    val upcomingAlarmDAO = AppDatabase.getInstance(this@ReminderService).getUpcomingReminderDao()
                    val query = upcomingAlarmDAO.getAll()
                    val upcomingAlarm = query.find {
                        SharedMethods.getLocalDateTimeFromJSDate(it.activateDateString).hour == now.hour &&
                                SharedMethods.getLocalDateTimeFromJSDate(it.activateDateString).minute == now.minute
                    }
                    if (upcomingAlarm != null) {
                        if (!isNotificationVisible(upcomingAlarm.notificationId)) {
                            Log.d(TAG, "Sending reminder notification...")
                            defineNotification(upcomingAlarm.medicineName)
                            notificationManager.notify(upcomingAlarm.notificationId, reminderNotification)
                        }
                        else Log.d(TAG, "Reminder notification is present.")
                    }
                    postDelayed(this, 1000)
                }
            }
            postDelayed(runnable, 1000)
        }
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

        fun startService(context: Context) {
            val intent = Intent(context, ReminderService::class.java)
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, ReminderService::class.java)
            context.stopService(intent)
        }
    }
}
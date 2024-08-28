package com.project.medibox.medication.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.project.medibox.R
import com.project.medibox.medication.persistence.UpcomingReminderAlarmDAO
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.SharedMethods
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random

class PostponeAlarmReceiver : BroadcastReceiver() {

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

    override fun onReceive(context: Context, intent: Intent?) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val notificationId = intent?.getStringExtra("notificationId")?.toInt()
        if (notificationId != null) {
            val dao = AppDatabase.getInstance(context).getUpcomingReminderAlarmDao()
            val alarm = dao.getAlarmByNotificationId(notificationId)
            if (alarm != null) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
                var localDateTime = LocalDateTime.now()
                localDateTime = localDateTime.plusMinutes(10)
                alarm.activateHour = localDateTime.hour
                alarm.activateMinute = localDateTime.minute
                alarm.activateDateString = SharedMethods.getDDMMYYStringFromDate(localDateTime)
                alarm.notified = false
                alarm.notificationId = generateNotificationId(dao)
                dao.updateAlarm(alarm)
                Log.d("PostponeAlarmReceiver", "Alarm ${alarm.id} postponed")
            }
        }
    }
}
package com.project.medibox.medication.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.project.medibox.medication.models.MissedReminderAlarm
import com.project.medibox.shared.AppDatabase

class MissAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val notificationId = intent?.getStringExtra("notificationId")?.toInt()
        if (notificationId != null) {
            val upcomingDao = AppDatabase.getInstance(context).getUpcomingReminderAlarmDao()
            val missedDao = AppDatabase.getInstance(context).getMissedReminderAlarmDao()
            val upcomingAlarm = upcomingDao.getAlarmByNotificationId(notificationId)
            if (upcomingAlarm != null) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
                upcomingDao.deleteById(upcomingAlarm.id)
                missedDao.insertAlarm(
                    MissedReminderAlarm(
                        0,
                        upcomingAlarm.medicineName,
                        upcomingAlarm.activateDateString,
                        upcomingAlarm.activateHour,
                        upcomingAlarm.activateMinute,
                        upcomingAlarm.consumeFood
                    )
                )
            }
        }
    }
}
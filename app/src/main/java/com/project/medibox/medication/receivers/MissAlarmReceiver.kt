package com.project.medibox.medication.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.project.medibox.R
import com.project.medibox.medication.models.ApiAlarm
import com.project.medibox.medication.models.MissedReminderAlarm
import com.project.medibox.medication.network.MedicationApiService
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MissAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val notificationId = intent?.getStringExtra("notificationId")?.toInt()
        if (notificationId != null) {
            val upcomingDao = AppDatabase.getInstance(context).getUpcomingReminderAlarmDao()
            val missedDao = AppDatabase.getInstance(context).getMissedReminderAlarmDao()
            val upcomingAlarm = upcomingDao.getAlarmByNotificationId(notificationId)
            val medicationApiService = SharedMethods.retrofitServiceBuilder(MedicationApiService::class.java)
            if (upcomingAlarm != null) {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
                upcomingDao.deleteById(upcomingAlarm.id)
                val missedAlarm = MissedReminderAlarm(
                    0,
                    upcomingAlarm.medicineName,
                    upcomingAlarm.activateDateString,
                    upcomingAlarm.activateHour,
                    upcomingAlarm.activateMinute,
                    upcomingAlarm.consumeFood
                )
                missedDao.insertAlarm(missedAlarm)
                val apiAlarm = SharedMethods.mapAlarmToCreateApiAlarmRes(missedAlarm, StateManager.loggedUserId)
                val request = medicationApiService.saveMissedAlarm(StateManager.authToken, apiAlarm)
                request.enqueue(object : Callback<ApiAlarm> {
                    override fun onResponse(p0: Call<ApiAlarm>, p1: Response<ApiAlarm>) {

                    }

                    override fun onFailure(p0: Call<ApiAlarm>, p1: Throwable) {
                        Toast.makeText(context,
                            context.getString(R.string.error_while_saving_the_alarm_in_cloud), Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }
    }
}
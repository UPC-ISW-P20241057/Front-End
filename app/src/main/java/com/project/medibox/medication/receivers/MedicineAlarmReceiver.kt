package com.project.medibox.medication.receivers

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.project.medibox.R
import com.project.medibox.medication.controller.activities.MedicationAlarmActivity
import com.project.medibox.shared.StateManager

class MedicineAlarmReceiver: BroadcastReceiver() {
    private val CHANNEL_ID = "1003"
    private lateinit var pendingIntent: PendingIntent
    private var medicationName: String = ""
        get() = field
        set(value) {
            field = value
        }
    private var notificationId: Int = -1
        get() = field
        set(value) {
            field = value
        }

    private fun createNotificationChannel(context: Context, intent: Intent) {
        if (!StateManager.isAlarmChannelCreated) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Medicine Alarm Receiver Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            val nextActivity = Intent(context, MedicationAlarmActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            pendingIntent = PendingIntent.getActivity(context, 0, nextActivity, PendingIntent.FLAG_IMMUTABLE)

            StateManager.isAlarmChannelCreated = true
        }
    }

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context, intent)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_test_notification)
            .setContentTitle("Time for medication!")
            .setContentText(medicationName)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(notificationId, notification)
    }
}
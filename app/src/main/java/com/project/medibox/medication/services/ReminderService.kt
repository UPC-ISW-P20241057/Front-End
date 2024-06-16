package com.project.medibox.medication.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder

class ReminderService : Service() {

    private val TAG = "ReminderService"
    private val handler = Handler()
    private lateinit var reminderNotification: Notification
    private var isStarted = false

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}
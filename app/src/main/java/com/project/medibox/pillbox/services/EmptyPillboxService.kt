package com.project.medibox.pillbox.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.project.medibox.R

class EmptyPillboxService : Service() {
    private val TAG = "EmptyPillboxService"
    private val handler = Handler()

    private lateinit var notificationManager: NotificationManager
    private var isStarted = false

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent): IBinder? {
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
            .setContentTitle("Empty Pillbox Service")
            .setContentText("Service running...")
            .setSmallIcon(R.drawable.ic_test_notification)
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }

    private fun createServiceNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Empty Pillbox Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun serviceLogic() {
        handler.apply {
            val runnable = object : Runnable {
                override fun run() {
                    Log.d(TAG, "Servicio corriendo jiji")
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

    companion object {
        private const val ONGOING_NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "1001"

        fun startService(context: Context) {
            val intent = Intent(context, EmptyPillboxService::class.java)
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, EmptyPillboxService::class.java)
            context.stopService(intent)
        }
    }
}
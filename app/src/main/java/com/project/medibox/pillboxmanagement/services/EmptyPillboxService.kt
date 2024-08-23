package com.project.medibox.pillboxmanagement.services

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
import com.project.medibox.pillboxmanagement.controller.activities.AlmostEmptyAlarmActivity
import com.project.medibox.pillboxmanagement.controller.activities.EmptyAlarmActivity
import com.project.medibox.pillboxmanagement.models.Pillbox
import com.project.medibox.pillboxmanagement.network.PillboxApiService
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmptyPillboxService : Service() {
    private val TAG = "EmptyPillboxService"
    private val handler = Handler()

    private lateinit var notificationManager: NotificationManager
    private lateinit var pillboxService: PillboxApiService
    private lateinit var emptyNotification: Notification
    private lateinit var almostEmptyNotification: Notification
    private var isStarted = false


    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        pillboxService = SharedMethods.retrofitServiceBuilder(PillboxApiService::class.java)
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
            .setSmallIcon(R.drawable.ic_stat_name)
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
        defineNotifications()
    }

    private fun defineNotifications() {
        emptyNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Oh no! Your pillbox is empty")
            .setContentText("Please refill your medications.")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentIntent(createPendingIntent(EmptyAlarmActivity::class.java))
            .setAutoCancel(true)
            .build()


        almostEmptyNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Your pillbox is near to be empty")
            .setContentText("Don't forget to refill your medications.")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentIntent(createPendingIntent(AlmostEmptyAlarmActivity::class.java))
            .setAutoCancel(true)
            .build()

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
                    if (!isNotificationVisible(EMPTY_NOTIFICATION_ID) && !isNotificationVisible(ALMOST_EMPTY_NOTIFICATION_ID)) {
                        Log.d(TAG, "Making http request to pillbox endpoint...")
                        val request = pillboxService.getPillboxData(1)
                        request.enqueue(object : Callback<Pillbox> {
                            override fun onResponse(call: Call<Pillbox>, response: Response<Pillbox>) {
                                if (response.isSuccessful) {
                                    if (response.body()!!.isEmpty) {
                                        notificationManager.notify(EMPTY_NOTIFICATION_ID, emptyNotification)
                                    }
                                    else if (response.body()!!.almostEmpty) {
                                        notificationManager.notify(ALMOST_EMPTY_NOTIFICATION_ID, almostEmptyNotification)
                                    }
                                }
                            }
                            override fun onFailure(p0: Call<Pillbox>, p1: Throwable) {

                            }
                        })
                    }
                    else Log.d(TAG, "Notification made. Aborting request...")
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
        private const val ONGOING_NOTIFICATION_ID = 101
        private const val EMPTY_NOTIFICATION_ID = 102
        private const val ALMOST_EMPTY_NOTIFICATION_ID = 103
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
package com.project.medibox.identitymanagement.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.project.medibox.R
import com.project.medibox.identitymanagement.models.AuthenticationRequest
import com.project.medibox.identitymanagement.models.AuthenticationResponse
import com.project.medibox.identitymanagement.network.UserApiService
import com.project.medibox.pillboxmanagement.models.Pillbox
import com.project.medibox.pillboxmanagement.services.EmptyPillboxService
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PermanentLoginService : Service() {
    private val TAG = "PermanentLoginService"
    private val handler = Handler()
    private lateinit var notificationManager: NotificationManager
    private lateinit var userService: UserApiService
    private var isStarted = false

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        userService = SharedMethods.retrofitServiceBuilder(UserApiService::class.java)
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

    private fun serviceLogic() {
        handler.apply {
            val runnable = object : Runnable {
                override fun run() {
                    val query = AppDatabase.getInstance(this@PermanentLoginService).getLoginCredentialsDao().getAll()
                    if (query.isNotEmpty()) {
                        Log.d(TAG, "The auth token has expired. Getting a new one from Login endpoint...")
                        val loginCredentials = query[0]
                        val userApiService = SharedMethods.retrofitServiceBuilder(UserApiService::class.java)

                        val request = userApiService.signIn(AuthenticationRequest(loginCredentials.email, loginCredentials.password))

                        request.enqueue(object : Callback<AuthenticationResponse> {
                            override fun onResponse(
                                call: Call<AuthenticationResponse>,
                                response: Response<AuthenticationResponse>
                            ) {
                                if (response.isSuccessful) {
                                    StateManager.authToken = response.body()!!.jwtToken
                                }
                            }
                            override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {}
                        })
                    }
                    postDelayed(this, 1230000)
                }
            }
            postDelayed(runnable, 1230000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isStarted = false
        Log.d(TAG, "Service destroyed.")
        handler.removeCallbacksAndMessages(null)
    }

    private fun makeForeground() {
        createServiceNotificationChannel()
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Permanent Login Service")
            .setContentText("Service running...")
            .setSmallIcon(R.drawable.ic_stat_name)
            .build()
        startForeground(ONGOING_NOTIFICATION_ID, notification)
    }
    private fun createServiceNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Permanent Login Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val ONGOING_NOTIFICATION_ID = 104
        private const val CHANNEL_ID = "1002"

        fun startService(context: Context) {
            val intent = Intent(context, PermanentLoginService::class.java)
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, PermanentLoginService::class.java)
            context.stopService(intent)
        }
    }
}
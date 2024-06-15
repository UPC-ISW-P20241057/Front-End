package com.project.medibox.shared

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.concurrent.TimeUnit


object SharedMethods {
    fun getJSDate(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return formatter.format(date)
    }
    fun getJSDateFromLocalDateTime(localDateTime: LocalDateTime): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        val date = Date.from(instant)
        return formatter.format(date)
    }
    fun localDateTimeToDate(localDateTime: LocalDateTime): Date {
        val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        val date = Date.from(instant)
        return date
    }
    private fun retrofitBuilder(): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(90, TimeUnit.SECONDS) // Tiempo de espera para leer
            .writeTimeout(90, TimeUnit.SECONDS) // Tiempo de espera para escribir
            .connectTimeout(90, TimeUnit.SECONDS) // Tiempo de espera para conectar
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://medibox-gateway-devel.azurewebsites.net/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    fun <T> retrofitServiceBuilder(service: Class<T>): T {
        return retrofitBuilder().create(service)
    }

}
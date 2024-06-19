package com.project.medibox.shared

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
    fun getLocalDateTimeFromJSDate(jsDate: String): LocalDateTime {
        val offsetDate = OffsetDateTime.parse(jsDate, DateTimeFormatter.ISO_DATE_TIME)
        return offsetDate.toLocalDateTime()
    }
    fun localDateTimeToDate(localDateTime: LocalDateTime): Date {
        val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        val date = Date.from(instant)
        return date
    }
    fun getDDMMYYStringFromDate(localDateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return localDateTime.format(formatter)
    }
    fun getDDMMYYStringFromDate(localDate: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return localDate.format(formatter)
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
    fun formatHourMinute24H(hour: Int, minute: Int): String {
        val formattedHour = hour.toString().padStart(2, '0')
        val formattedMinute = minute.toString().padStart(2, '0')
        return "$formattedHour:$formattedMinute"
    }
    fun formatHourMinute12He(hour: Int, minute: Int): String {
        val formattedHour = if (hour >= 12) {
            val adjustedHour = if (hour > 12) hour - 12 else hour
            "$adjustedHour:$minute PM"
        } else {
            "$hour:$minute AM"
        }
        return formattedHour.padStart(8, '0')
    }
    fun formatHourMinute12H(hour: Int, minute: Int): String {
        val finalHour: Int
        val moment: String
        if (hour >= 12) {
            finalHour = if (hour > 12) hour - 12 else hour
            moment = "PM"
        }
        else {
            finalHour = if (hour == 0) 12 else hour
            moment = "PM"
        }
        val hourStr = if (finalHour < 10) "0${finalHour}" else finalHour.toString()
        val minuteStr = if (minute < 10) "0${minute}" else minute.toString()

        return "$hourStr:$minuteStr $moment"
    }
}
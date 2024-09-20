package com.project.medibox.shared

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.project.medibox.R
import com.project.medibox.medication.models.ApiAlarm
import com.project.medibox.medication.models.CompletedReminderAlarm
import com.project.medibox.medication.models.MissedReminderAlarm
import com.project.medibox.medication.resources.CreateApiAlarmResource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.TimeUnit


object SharedMethods {
    private const val TAG = "SharedMethods"

    fun getJSDate(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return formatter.format(date)
    }
    fun getJSDateFromLocalDateTime(localDateTime: LocalDateTime): String {
        val offsetDate = OffsetDateTime.of(localDateTime, ZoneOffset.UTC)
        return offsetDate.format(DateTimeFormatter.ISO_DATE_TIME)
    }
    fun getLocalDateTimeFromJSDateeee(jsDate: String): LocalDateTime {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val date =  LocalDateTime.parse(jsDate, formatter)
        return date
    }
    fun getLocalDateTimeFromJSDate(jsDate: String): LocalDateTime {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        return LocalDateTime.parse(jsDate, formatter)
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
            moment = "AM"
        }
        val hourStr = if (finalHour < 10) "0${finalHour}" else finalHour.toString()
        val minuteStr = if (minute < 10) "0${minute}" else minute.toString()

        Log.d(TAG, "$hourStr:$minuteStr $moment")
        return "$hourStr:$minuteStr $moment"
    }
    fun convertDDMMYYYYToLocalDate(date: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return LocalDate.parse(date, formatter)
    }
    fun isDarkTheme(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
            return false
        return activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
    fun isStringAnULong(string: String): Boolean {
        try {
            string.toULong()
            return true
        }
        catch (e: NumberFormatException) {
            return false
        }
    }
    fun mapApiAlarmListToCompletedAlarmList(apiAlarmList: List<ApiAlarm>): List<CompletedReminderAlarm> {
        return apiAlarmList.map {
            CompletedReminderAlarm(
                0,
                it.medicineName,
                it.activateDateString,
                it.activateHour,
                it.activateMinute,
                it.consumeFood
            )
        }
    }
    fun mapApiAlarmListToMissedAlarmList(apiAlarmList: List<ApiAlarm>): List<MissedReminderAlarm> {
        return apiAlarmList.map {
            MissedReminderAlarm(
                0,
                it.medicineName,
                it.activateDateString,
                it.activateHour,
                it.activateMinute,
                it.consumeFood
            )
        }
    }
    fun mapAlarmToCreateApiAlarmRes(alarm: CompletedReminderAlarm, userId: Long = 0): CreateApiAlarmResource {
        return CreateApiAlarmResource(
            alarm.medicineName,
            alarm.activateDateString,
            alarm.activateHour,
            alarm.activateMinute,
            alarm.consumeFood,
            userId
        )
    }
    fun mapAlarmToCreateApiAlarmRes(alarm: MissedReminderAlarm, userId: Long = 0): CreateApiAlarmResource {
        return CreateApiAlarmResource(
            alarm.medicineName,
            alarm.activateDateString,
            alarm.activateHour,
            alarm.activateMinute,
            alarm.consumeFood,
            userId
        )
    }
    fun containsOnlyLettersAndSpaces(input: String): Boolean {
        return input.all { it.isLetter() || it.isWhitespace() }
    }
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }
    fun registrationValidationToasts(context: Context,
                                     emailValidation: Boolean,
                                     stringsNotEmptyValidation: Boolean,
                                     passwordValidation: Boolean,
                                     numberValidation: Boolean,
                                     nameValidation: Boolean) {
        Toast.makeText(context,
            context.getString(R.string.validation_errors_occurred), Toast.LENGTH_SHORT).show()
        if (!stringsNotEmptyValidation)
            Toast.makeText(context,
                context.getString(R.string.empty_fields), Toast.LENGTH_SHORT).show()
        else {
            if (!emailValidation)
                Toast.makeText(context,
                    context.getString(R.string.invalid_email_format), Toast.LENGTH_SHORT).show()
            if (!passwordValidation)
                Toast.makeText(context, context.getString(R.string.password_not_match), Toast.LENGTH_SHORT).show()
            if (!numberValidation)
                Toast.makeText(context,
                    context.getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show()
            if (!nameValidation)
                Toast.makeText(context, context.getString(R.string.name_and_lastname_only_have_letters), Toast.LENGTH_SHORT).show()
        }
    }
    fun updateUserValidationToasts(context: Context,
                                   emailValidation: Boolean,
                                   stringsNotEmptyValidation: Boolean,
                                   numberValidation: Boolean,
                                   nameValidation: Boolean) {
        Toast.makeText(context,
            context.getString(R.string.validation_errors_occurred), Toast.LENGTH_SHORT).show()
        if (!stringsNotEmptyValidation)
            Toast.makeText(context,
                context.getString(R.string.empty_fields), Toast.LENGTH_SHORT).show()
        else {
            if (!emailValidation)
                Toast.makeText(context,
                    context.getString(R.string.invalid_email_format), Toast.LENGTH_SHORT).show()
            if (!numberValidation)
                Toast.makeText(context,
                    context.getString(R.string.invalid_phone_number), Toast.LENGTH_SHORT).show()
            if (!nameValidation)
                Toast.makeText(context, context.getString(R.string.name_and_lastname_only_have_letters), Toast.LENGTH_SHORT).show()
        }
    }
    fun isValidNumberString(input: String): Boolean {
        val numberRegex = "^[0-9]{1,9}$".toRegex()
        return input.matches(numberRegex) && input.length == 9
    }
}
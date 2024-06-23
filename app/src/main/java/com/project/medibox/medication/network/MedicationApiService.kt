package com.project.medibox.medication.network

import com.project.medibox.medication.models.*
import com.project.medibox.medication.resources.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface MedicationApiService {
    @GET("gateway/v1/medicines")
    fun getAllMedicines(@Header("Authorization") token: String): Call<List<Medicine>>
    @GET("gateway/v1/people/{userId}/reminders")
    fun getRemindersByUserId(@Header("Authorization") token: String, @Path("userId") userId: Long): Call<List<Reminder>>
    @POST("gateway/v1/reminders")
    fun createReminder(@Header("Authorization") token: String, @Body reminder: CreateReminderResource): Call<Reminder>
    @POST("gateway/v1/intervals")
    fun createInterval(@Header("Authorization") token: String, @Body interval: CreateIntervalResource): Call<Interval>
    @POST("gateway/v1/frequencies")
    fun createFrequency(@Header("Authorization") token: String, @Body frequency: CreateFrequencyResource): Call<Frequency>
    @PUT("gateway/v1/reminders/{id}")
    fun updateReminder(@Header("Authorization") token: String, @Path("id") id: Long, @Body reminder: CreateReminderResource): Call<Reminder>
    @PUT("gateway/v1/frequencies/{id}")
    fun updateFrequency(@Header("Authorization") token: String, @Path("id") id: Long, @Body frequency: CreateFrequencyResource): Call<Frequency>
    @PUT("gateway/v1/intervals/{id}")
    fun updateInterval(@Header("Authorization") token: String, @Path("id") id: Long, @Body interval: CreateIntervalResource): Call<Interval>
    @DELETE("gateway/v1/reminders/{id}")
    fun deleteReminder(@Header("Authorization") token: String, @Path("id") id: Long): Call<Reminder>
}
package com.project.medibox.medication.network

import com.project.medibox.medication.resources.CreateIntervalResource
import com.project.medibox.medication.models.Medicine
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MedicationService {
    @GET("gateway/v1/medicines")
    fun getAllMedicines(@Header("Authorization") token: String): Call<List<Medicine>>
    @POST("gateway/v1/intervals")
    fun createInterval(@Header("Authorization") token: String, @Body interval: CreateIntervalResource)
}
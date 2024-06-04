package com.project.medibox.medication.controller.fragments.network

import com.project.medibox.medication.controller.fragments.models.Medicine
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface MedicationService {
    @POST("gateway/v1/medicines")
    fun getAllMedicines(@Header("Authorization") token: String): Call<List<Medicine>>
}
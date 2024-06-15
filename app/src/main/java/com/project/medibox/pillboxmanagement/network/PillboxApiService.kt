package com.project.medibox.pillboxmanagement.network

import com.project.medibox.pillboxmanagement.models.Pillbox
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface PillboxApiService {
    @GET("gateway/v1/weights/{id}")
    fun getPillboxData(@Path("id") id: Long): Call<Pillbox>
}
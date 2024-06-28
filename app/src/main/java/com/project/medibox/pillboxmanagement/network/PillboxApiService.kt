package com.project.medibox.pillboxmanagement.network

import com.project.medibox.pillboxmanagement.models.BoxData
import com.project.medibox.pillboxmanagement.models.BoxDataResponse
import com.project.medibox.pillboxmanagement.models.Pillbox
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface PillboxApiService {
    @GET("gateway/v1/weights/{id}")
    fun getPillboxData(@Path("id") id: Long): Call<Pillbox>

    @PATCH("gateway/v1/weights/{id}")
    fun updatePillboxData(@Path("id") id: Long, @Body boxData: BoxData): Call<BoxDataResponse>
}
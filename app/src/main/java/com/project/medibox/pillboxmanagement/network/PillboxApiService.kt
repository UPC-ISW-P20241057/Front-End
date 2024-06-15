package com.project.medibox.pillboxmanagement.network

import com.project.medibox.pillboxmanagement.models.Pillbox
import retrofit2.Call
import retrofit2.http.GET

interface PillboxApiService {
    @GET("gateway/v1/weights/latest")
    fun getPillboxData(): Call<Pillbox>
}
package com.project.medibox.shared

import androidx.annotation.NonNull
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit


object SharedMethods {
    fun getJSDate(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        return formatter.format(date)
    }
    private fun retrofitBuilder(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(90, TimeUnit.SECONDS) // Tiempo de espera para leer
            .writeTimeout(90, TimeUnit.SECONDS) // Tiempo de espera para escribir
            .connectTimeout(90, TimeUnit.SECONDS) // Tiempo de espera para conectar
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
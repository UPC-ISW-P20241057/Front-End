package com.project.medibox.identitymanagement.network

import com.project.medibox.identitymanagement.models.AuthenticationRequest
import com.project.medibox.identitymanagement.models.AuthenticationResponse
import com.project.medibox.identitymanagement.models.RegisterRequest
import com.project.medibox.identitymanagement.models.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("gateway/v1/users/sign-in")
    fun signIn(@Body request: AuthenticationRequest): Call<AuthenticationResponse>
    @POST("gateway/v1/users/sign-up")
    fun signUp(@Body request: RegisterRequest): Call<RegisterResponse>
}
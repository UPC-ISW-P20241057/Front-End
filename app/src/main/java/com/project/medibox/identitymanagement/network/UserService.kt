package com.project.medibox.identitymanagement.network

import com.project.medibox.identitymanagement.models.AuthenticationRequest
import com.project.medibox.identitymanagement.models.AuthenticationResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("gateway/v1/users/sign-in")
    fun signIn(@Body request: AuthenticationRequest): Call<AuthenticationResponse>
}
package com.project.medibox.identitymanagement.network

import com.project.medibox.identitymanagement.models.AuthenticationRequest
import com.project.medibox.identitymanagement.models.AuthenticationResponse
import com.project.medibox.identitymanagement.models.RegisterRequest
import com.project.medibox.identitymanagement.models.RegisterResponse
import com.project.medibox.identitymanagement.models.UpdateRequest
import com.project.medibox.identitymanagement.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    @POST("gateway/v1/users/sign-in")
    fun signIn(@Body request: AuthenticationRequest): Call<AuthenticationResponse>
    @POST("gateway/v1/users/sign-up")
    fun signUp(@Body request: RegisterRequest): Call<RegisterResponse>
    @PUT("gateway/v1/users/{id}")
    fun updateUser(@Header("Authorization") token: String, @Path("id") id: Long, @Body updateRequest: UpdateRequest): Call<User>
}
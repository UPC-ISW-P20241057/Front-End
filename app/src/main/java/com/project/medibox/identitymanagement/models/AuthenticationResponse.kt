package com.project.medibox.identitymanagement.models

import com.google.gson.annotations.SerializedName

data class AuthenticationResponse(
    @SerializedName("id")
    var id: Long,
    @SerializedName("email")
    var email: String,
    @SerializedName("role")
    var role: String,
    @SerializedName("phone")
    var phone: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("lastName")
    var lastName: String,
    @SerializedName("jwtToken")
    var jwtToken: String,
    @SerializedName("expiresIn")
    var expiresIn: Int
)

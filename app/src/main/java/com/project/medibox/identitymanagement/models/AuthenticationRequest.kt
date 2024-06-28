package com.project.medibox.identitymanagement.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AuthenticationRequest(
    @SerializedName("email")
    @Expose
    var email: String,
    @SerializedName("password")
    @Expose
    var password: String
)

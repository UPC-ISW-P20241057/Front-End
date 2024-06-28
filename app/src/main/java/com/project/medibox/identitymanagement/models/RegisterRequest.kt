package com.project.medibox.identitymanagement.models

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    var email: String,
    var password: String,
    var role: String,
    var phone: String,
    var name: String,
    var lastName: String
)

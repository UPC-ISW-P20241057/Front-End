package com.project.medibox.identitymanagement.models

import com.google.gson.annotations.SerializedName

data class User(
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
    var lastName: String
)
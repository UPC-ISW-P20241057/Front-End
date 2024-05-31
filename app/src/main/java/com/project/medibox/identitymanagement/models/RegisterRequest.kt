package com.project.medibox.identitymanagement.models

data class RegisterRequest(
    var email: String,
    var password: String,
    var role: String,
    var phone: String,
    var name: String,
    var lastName: String
)

package com.project.medibox.identitymanagement.models

data class UpdateRequest(
    var email: String,
    var password: String,
    var phone: String,
    var name: String,
    var lastName: String
)

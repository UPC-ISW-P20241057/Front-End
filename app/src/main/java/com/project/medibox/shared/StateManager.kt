package com.project.medibox.shared

import com.project.medibox.identitymanagement.models.User

object StateManager {
    lateinit var authToken: String
    var loggedUserId: Long = -1
    lateinit var loggedUser: User
}
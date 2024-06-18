package com.project.medibox.shared

import com.project.medibox.identitymanagement.models.User
import com.project.medibox.medication.models.Medicine

object StateManager {
    lateinit var authToken: String
    var loggedUserId: Long = -1
    lateinit var loggedUser: User
    var selectedMedicine: Medicine? = null
    var isAlarmChannelCreated: Boolean = false
}
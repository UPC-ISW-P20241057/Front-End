package com.project.medibox.pillboxmanagement.models

data class BoxData(
    var id: Long,
    var reminder: Boolean,
    var numberAlarm: Int = 2,
    var ssid: String = "",
    var password: String = ""
)

package com.project.medibox.pillboxmanagement.models

data class BoxDataResponse(
    var reminder: Boolean,
    var isEmpty: Boolean,
    var almostEmpty: Boolean,
    var numberAlarm: Int,
    var ssid: String,
    var password: String)
package com.project.medibox.pillboxmanagement.models

import com.google.gson.annotations.SerializedName

data class Pillbox(
    var id: Long,
    @SerializedName("value")
    var weight: String,
    var reminder: Boolean,
    var isEmpty: Boolean,
    var almostEmpty: Boolean,
    var numberAlarm: Int
)

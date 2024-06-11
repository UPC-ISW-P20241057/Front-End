package com.project.medibox.medication.models

import com.google.gson.annotations.SerializedName

data class Interval (
    var id: Long,
    var intervalType: String,

    @SerializedName("interval")
    var intervalValue: Int
)
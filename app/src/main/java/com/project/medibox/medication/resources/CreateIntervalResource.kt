package com.project.medibox.medication.resources

import com.google.gson.annotations.SerializedName

data class CreateIntervalResource(
    @SerializedName("intervalType")
    var type: String,
    @SerializedName("value")
    var quantity: Int,
    var reminderId: Long
)

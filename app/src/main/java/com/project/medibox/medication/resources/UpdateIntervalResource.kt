package com.project.medibox.medication.resources

import com.google.gson.annotations.SerializedName

data class UpdateIntervalResource(
    var id: Long,
    @SerializedName("intervalType")
    var type: String,
    @SerializedName("interval")
    var quantity: Int,
    var reminderId: Long
)

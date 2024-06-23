package com.project.medibox.medication.resources

import com.google.gson.annotations.SerializedName

data class UpdateFrequencyResource(
    var id: Long,
    @SerializedName("frequencyType")
    var type: String,
    var times: Int,
    var reminderId: Long
)
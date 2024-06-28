package com.project.medibox.medication.models

import com.google.gson.annotations.SerializedName

data class Reminder(
    var id: Long,
    @SerializedName("createdDate")
    var createdDateString: String,
    var pills: Short?,
    @SerializedName("endDate")
    var endDateString: String?,
    var medicine: Medicine,
    var userId: Long,
    var frequency: Frequency?,
    var interval: Interval?,
    var consumeFood: Boolean?
)
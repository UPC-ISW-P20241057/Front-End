package com.project.medibox.medication.models

import com.google.gson.annotations.SerializedName

data class Reminder(
    var id: Long,
    @SerializedName("createdDate")
    var createdDateString: String,
    var pills: Int,
    @SerializedName("endDate")
    var endDateString: String,
    var medicineId: Long,
    var userId: Long
)
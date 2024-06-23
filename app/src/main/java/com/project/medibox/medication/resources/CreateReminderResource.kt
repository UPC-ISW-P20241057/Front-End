package com.project.medibox.medication.resources

import com.google.gson.annotations.SerializedName

data class CreateReminderResource(
    @SerializedName("createdDate")
    var createdDateString: String,
    var pills: Short?,
    @SerializedName("endDate")
    var endDateString: String?,
    var medicineId: Long,
    var userId: Long,
    var consumeFood: Boolean?
)

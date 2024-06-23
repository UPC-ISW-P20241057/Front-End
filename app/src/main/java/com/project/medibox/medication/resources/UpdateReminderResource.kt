package com.project.medibox.medication.resources

import com.google.gson.annotations.SerializedName

data class UpdateReminderResource(
    var id: Long,
    @SerializedName("createdDate")
    var createdDateString: String,
    var pills: Int?,
    @SerializedName("endDate")
    var endDateString: String?,
    var medicineId: Long,
    var userId: Long,
    var consumeFood: Boolean?
)
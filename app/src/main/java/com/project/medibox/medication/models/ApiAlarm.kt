package com.project.medibox.medication.models

data class ApiAlarm(
    var id: Long,
    var medicineName: String,
    var activateDateString: String,
    var activateHour: Int,
    var activateMinute: Int,
    var consumeFood: Boolean?
)
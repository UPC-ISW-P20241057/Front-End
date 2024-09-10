package com.project.medibox.medication.resources

data class CreateApiAlarmResource(
    var medicineName: String,
    var activateDateString: String,
    var activateHour: Int,
    var activateMinute: Int,
    var consumeFood: Boolean?,
    var userId: Long
)

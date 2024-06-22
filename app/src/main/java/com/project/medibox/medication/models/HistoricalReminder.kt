package com.project.medibox.medication.models

import androidx.room.Entity

@Entity
class HistoricalReminder(
    var id: Int,
    var createdDateString: String,
    var pills: Int?,
    var endDateString: String,
    var medicineName: String,
    var type: String,
    var consumeFood: Boolean?,
    var reminderId: Long
)
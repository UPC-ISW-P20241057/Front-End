package com.project.medibox.medication.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class HistoricalReminder(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var createdDateString: String,
    var pills: Int?,
    var endDateString: String,
    var medicineName: String,
    var type: String,
    var typeId: Long,
    var consumeFood: Boolean?,
    var reminderId: Long
)
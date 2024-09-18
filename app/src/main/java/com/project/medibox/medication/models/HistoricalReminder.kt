package com.project.medibox.medication.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class HistoricalReminder(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var createdDate: String,
    var createdDateSimply: String,
    var pills: Short?,
    var endDateString: String,
    var endDateStringSimply: String,
    var medicineName: String,
    var type: String,
    var typeId: Long,
    var consumeFood: Boolean?,
    var reminderId: Long,
    var localId: Short = -1
)
package com.project.medibox.medication.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UpcomingReminderAlarm(
    @PrimaryKey(autoGenerate = true)
    var id: Short,
    @ColumnInfo
    var medicineName: String,
    @ColumnInfo
    var activateDateString: String,
    @ColumnInfo
    var consumeFood: Boolean?,
    @ColumnInfo
    var reminderId: Long
)

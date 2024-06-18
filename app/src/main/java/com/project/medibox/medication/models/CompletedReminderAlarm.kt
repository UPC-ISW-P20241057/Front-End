package com.project.medibox.medication.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CompletedReminderAlarm(
    @PrimaryKey(autoGenerate = true)
    var id: Short,
    @ColumnInfo
    var medicineName: String,
    @ColumnInfo
    var activateDateString: String,
    @ColumnInfo
    var activateHour: Int,
    @ColumnInfo
    var activateMinute: Int,
    @ColumnInfo
    var consumeFood: Boolean?
)

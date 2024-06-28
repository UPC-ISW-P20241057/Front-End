package com.project.medibox.medication.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.project.medibox.medication.models.CompletedReminderAlarm

@Dao
interface CompletedReminderAlarmDAO {
    @Query("SELECT * FROM CompletedReminderAlarm")
    fun getAll(): List<CompletedReminderAlarm>

    @Insert
    fun insertAlarm(vararg alarm: CompletedReminderAlarm)

    @Query("DELETE FROM CompletedReminderAlarm")
    fun clearTable()
}
package com.project.medibox.medication.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.project.medibox.medication.models.HistoricalReminder

@Dao
interface HistoricalReminderDAO {
    @Query("SELECT * FROM HistoricalReminder")
    fun getAll(): List<HistoricalReminder>
    @Insert
    fun insertReminder(vararg reminder: HistoricalReminder)
}
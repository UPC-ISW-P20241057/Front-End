package com.project.medibox.medication.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.project.medibox.medication.models.HistoricalReminder

@Dao
interface HistoricalReminderDAO {
    @Query("SELECT * FROM HistoricalReminder")
    fun getAll(): List<HistoricalReminder>

    @Insert
    fun insertReminder(vararg reminder: HistoricalReminder)

    @Update
    fun updateReminder(reminder: HistoricalReminder)

    @Query("DELETE FROM HistoricalReminder")
    fun clearTable()

    @Query("DELETE FROM HistoricalReminder WHERE id = :id")
    fun deleteById(id: Int)

    @Query("SELECT * FROM HistoricalReminder WHERE localId = :localId")
    fun getByLocalId(localId: Short): HistoricalReminder?

    @Query("DELETE FROM HistoricalReminder WHERE localId = :localId")
    fun deleteByLocalId(localId: Short)
}

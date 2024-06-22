package com.project.medibox.medication.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.project.medibox.medication.models.StorableReminder

@Dao
interface StorableReminderDAO {
    @Query("SELECT * FROM StorableReminder")
    fun getAll(): List<StorableReminder>
    @Insert
    fun insertReminder(vararg reminder: StorableReminder)
}
package com.project.medibox.medication.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.project.medibox.medication.models.MissedReminderAlarm

@Dao
interface MissedReminderAlarmDAO {
    @Query("SELECT * FROM MissedReminderAlarm")
    fun getAll(): List<MissedReminderAlarm>

    @Insert
    fun insertAlarm(vararg alarm: MissedReminderAlarm)

    @Query("DELETE FROM MissedReminderAlarm")
    fun clearTable()
}
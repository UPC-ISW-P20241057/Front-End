package com.project.medibox.medication.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.project.medibox.medication.models.UpcomingReminderAlarm

@Dao
interface UpcomingReminderAlarmDAO {
    @Query("SELECT * FROM UpcomingReminderAlarm")
    fun getAll(): List<UpcomingReminderAlarm>

    @Insert
    fun insertAlarm(vararg alarm: UpcomingReminderAlarm)

    @Query("UPDATE UpcomingReminderAlarm " +
            "SET notified = 1 " +
            "WHERE id = :id")
    fun setNotifiedById(id: Short)

    @Query("DELETE FROM UpcomingReminderAlarm WHERE reminderId = :reminderId")
    fun deleteAllByReminderId(reminderId: Long)

    @Query("DELETE FROM UpcomingReminderAlarm WHERE id = :id")
    fun deleteById(id: Short)
}
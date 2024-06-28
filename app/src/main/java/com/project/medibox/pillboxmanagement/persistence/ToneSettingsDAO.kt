package com.project.medibox.pillboxmanagement.persistence

import androidx.room.Dao
import androidx.room.Query
import com.project.medibox.pillboxmanagement.models.ToneSettings

@Dao
interface ToneSettingsDAO {

    @Query("SELECT * FROM ToneSettings WHERE id = 1")
    fun getSettings(): ToneSettings

    @Query("INSERT INTO ToneSettings VALUES(1,1)")
    fun createSettings()

    @Query("UPDATE ToneSettings SET tone = :tone WHERE id = 1")
    fun changeTone(tone: Byte)

    @Query("DELETE FROM ToneSettings")
    fun cleanSettings()
}
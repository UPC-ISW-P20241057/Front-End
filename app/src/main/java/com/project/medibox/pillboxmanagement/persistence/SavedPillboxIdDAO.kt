package com.project.medibox.pillboxmanagement.persistence

import androidx.room.Dao
import androidx.room.Query
import com.project.medibox.pillboxmanagement.models.SavedPillboxId

@Dao
interface SavedPillboxIdDAO {
    @Query("SELECT * FROM SavedPillboxId WHERE id = 1")
    fun getPillboxId(): SavedPillboxId?

    @Query("INSERT INTO SavedPillboxId VALUES(1,:pillBoxId)")
    fun savePillboxId(pillBoxId: Long)

    @Query("UPDATE SavedPillboxId SET pillBoxId = :pillBoxId WHERE id = 1")
    fun changePillboxId(pillBoxId: Long)

    @Query("DELETE FROM SavedPillboxId")
    fun deletePillboxId()
}
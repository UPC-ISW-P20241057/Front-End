package com.project.medibox.identitymanagement.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.project.medibox.identitymanagement.models.LoginCredentials

@Dao
interface LoginCredentialsDAO {
    @Query("SELECT * FROM LoginCredentials")
    fun getAll(): List<LoginCredentials>

    @Insert
    fun insertCredentials(vararg credentials: LoginCredentials)

    @Query("DELETE FROM LoginCredentials")
    fun cleanTable()
}
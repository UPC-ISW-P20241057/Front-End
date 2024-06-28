package com.project.medibox.identitymanagement.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LoginCredentials(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    @ColumnInfo
    var email: String,
    @ColumnInfo
    var password: String
)

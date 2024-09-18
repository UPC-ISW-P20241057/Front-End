package com.project.medibox.pillboxmanagement.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SavedPillboxId(
    @PrimaryKey(autoGenerate = false)
    var id: Byte,
    var pillBoxId: Long
)
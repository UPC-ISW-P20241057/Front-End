package com.project.medibox.medication.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MedicineImage(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    var imageString: String,
    var medicineName: String
)

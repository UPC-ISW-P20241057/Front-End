package com.project.medibox.medication.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.project.medibox.medication.models.MedicineImage

@Dao
interface MedicineImageDAO {
    @Query("SELECT * FROM MedicineImage WHERE medicineName = :medicineName")
    fun getImageByMedicineName(medicineName: String): MedicineImage

    @Insert
    fun insertImage(image: MedicineImage)

    @Update
    fun updateImage(image: MedicineImage)

    @Query("DELETE FROM MedicineImage WHERE medicineName = :medicineName")
    fun deleteImageByMedicineName(medicineName: String)
}
package com.project.medibox.shared

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.medibox.identitymanagement.models.LoginCredentials
import com.project.medibox.identitymanagement.persistence.LoginCredentialsDAO
import com.project.medibox.medication.models.CompletedReminderAlarm
import com.project.medibox.medication.models.MissedReminderAlarm
import com.project.medibox.medication.models.HistoricalReminder
import com.project.medibox.medication.models.MedicineImage
import com.project.medibox.medication.models.UpcomingReminderAlarm
import com.project.medibox.medication.persistence.CompletedReminderAlarmDAO
import com.project.medibox.medication.persistence.MissedReminderAlarmDAO
import com.project.medibox.medication.persistence.HistoricalReminderDAO
import com.project.medibox.medication.persistence.MedicineImageDAO
import com.project.medibox.medication.persistence.UpcomingReminderAlarmDAO
import com.project.medibox.pillboxmanagement.models.SavedPillboxId
import com.project.medibox.pillboxmanagement.models.ToneSettings
import com.project.medibox.pillboxmanagement.persistence.SavedPillboxIdDAO
import com.project.medibox.pillboxmanagement.persistence.ToneSettingsDAO

@Database(entities = [LoginCredentials::class,
    UpcomingReminderAlarm::class,
    CompletedReminderAlarm::class,
    MissedReminderAlarm::class,
    HistoricalReminder::class,
    ToneSettings::class,
    MedicineImage::class,
    SavedPillboxId::class],
    version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getLoginCredentialsDao(): LoginCredentialsDAO
    abstract fun getUpcomingReminderAlarmDao(): UpcomingReminderAlarmDAO
    abstract fun getCompletedReminderAlarmDao(): CompletedReminderAlarmDAO
    abstract fun getMissedReminderAlarmDao(): MissedReminderAlarmDAO
    abstract fun getHistoricalReminderDao(): HistoricalReminderDAO
    abstract fun getToneSettingsDao(): ToneSettingsDAO
    abstract fun getMedicineImageDao(): MedicineImageDAO
    abstract fun getSavedPillboxIdDao(): SavedPillboxIdDAO
    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room
                    .databaseBuilder(context, AppDatabase::class.java, "medibox.db")
                    .allowMainThreadQueries()
                    .build()
            }

            return INSTANCE as AppDatabase
        }
    }
}
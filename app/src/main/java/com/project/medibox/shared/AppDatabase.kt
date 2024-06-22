package com.project.medibox.shared

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.medibox.identitymanagement.models.LoginCredentials
import com.project.medibox.identitymanagement.persistence.LoginCredentialsDAO
import com.project.medibox.medication.models.CompletedReminderAlarm
import com.project.medibox.medication.models.MissedReminderAlarm
import com.project.medibox.medication.models.StorableReminder
import com.project.medibox.medication.models.UpcomingReminderAlarm
import com.project.medibox.medication.persistence.CompletedReminderAlarmDAO
import com.project.medibox.medication.persistence.MissedReminderAlarmDAO
import com.project.medibox.medication.persistence.StorableReminderDAO
import com.project.medibox.medication.persistence.UpcomingReminderAlarmDAO

@Database(entities = [LoginCredentials::class,
    UpcomingReminderAlarm::class,
    CompletedReminderAlarm::class,
    MissedReminderAlarm::class,
    StorableReminder::class],
    version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getLoginCredentialsDao(): LoginCredentialsDAO
    abstract fun getUpcomingReminderAlarmDao(): UpcomingReminderAlarmDAO
    abstract fun getCompletedReminderAlarmDao(): CompletedReminderAlarmDAO
    abstract fun getMissedReminderAlarmDao(): MissedReminderAlarmDAO
    abstract fun getStorableReminderDao(): StorableReminderDAO
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
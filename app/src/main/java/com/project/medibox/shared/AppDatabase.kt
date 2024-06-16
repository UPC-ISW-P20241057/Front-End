package com.project.medibox.shared

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.project.medibox.identitymanagement.models.LoginCredentials
import com.project.medibox.identitymanagement.persistence.LoginCredentialsDAO
import com.project.medibox.medication.models.UpcomingReminderAlarm

@Database(entities = [LoginCredentials::class, UpcomingReminderAlarm::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getLoginCredentialsDao(): LoginCredentialsDAO
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
package com.project.medibox.medication.controller.activities

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.medication.models.CompletedReminderAlarm
import com.project.medibox.medication.models.MissedReminderAlarm
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.StateManager

class MedicationAlarmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_medication_alarm)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnMediAccept = findViewById<Button>(R.id.btnMediAccept)
        val btnMediMissed = findViewById<Button>(R.id.btnMediMissed)

        val upcomingAlarm = StateManager.selectedUpcomingAlarm

        btnMediAccept.setOnClickListener {
            AppDatabase.getInstance(this).getUpcomingReminderAlarmDao().deleteById(upcomingAlarm.id)
            AppDatabase.getInstance(this).getCompletedReminderAlarmDao().insertAlarm(
                CompletedReminderAlarm(
                    0,
                    upcomingAlarm.medicineName,
                    upcomingAlarm.activateDateString,
                    upcomingAlarm.activateHour,
                    upcomingAlarm.activateMinute,
                    upcomingAlarm.consumeFood
                )
            )
            finish()
        }
        btnMediMissed.setOnClickListener {
            AppDatabase.getInstance(this).getUpcomingReminderAlarmDao().deleteById(StateManager.selectedUpcomingAlarm.id)
            AppDatabase.getInstance(this).getMissedReminderAlarmDao().insertAlarm(
                MissedReminderAlarm(
                    0,
                    upcomingAlarm.medicineName,
                    upcomingAlarm.activateDateString,
                    upcomingAlarm.activateHour,
                    upcomingAlarm.activateMinute,
                    upcomingAlarm.consumeFood
                )
            )
            finish()
        }
    }
}
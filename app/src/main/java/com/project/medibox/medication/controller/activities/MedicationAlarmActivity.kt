package com.project.medibox.medication.controller.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.medication.models.CompletedReminderAlarm
import com.project.medibox.medication.models.MissedReminderAlarm
import com.project.medibox.medication.persistence.UpcomingReminderAlarmDAO
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import java.time.LocalDateTime
import kotlin.random.Random

class MedicationAlarmActivity : AppCompatActivity() {
    private fun generateNotificationId(dao: UpcomingReminderAlarmDAO): Int {
        val existingIds = dao.getAll().map { it.notificationId }.toSet()
        val random = Random(System.nanoTime())
        while (true) {
            val notificationId = random.nextInt(Int.MAX_VALUE - 200 + 1) + 200
            if (notificationId !in existingIds) {
                return notificationId
            }
        }
    }
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
        val btnMediPostponed = findViewById<Button>(R.id.btnMediPostpone)
        val tvTimeForMedMedicine = findViewById<TextView>(R.id.tvTimeForMedMedicine)
        val upcomingAlarm = StateManager.selectedUpcomingAlarm
        tvTimeForMedMedicine.text = upcomingAlarm.medicineName

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
            AppDatabase.getInstance(this).getUpcomingReminderAlarmDao().deleteById(upcomingAlarm.id)
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
        btnMediPostponed.setOnClickListener {
            val dao = AppDatabase.getInstance(this).getUpcomingReminderAlarmDao()
            var localDateTime = LocalDateTime.now()
            localDateTime = localDateTime.plusMinutes(10)
            upcomingAlarm.activateHour = localDateTime.hour
            upcomingAlarm.activateMinute = localDateTime.minute
            upcomingAlarm.activateDateString = SharedMethods.getDDMMYYStringFromDate(localDateTime)
            upcomingAlarm.notified = false
            upcomingAlarm.notificationId = generateNotificationId(dao)
            dao.updateAlarm(upcomingAlarm)
            finish()
        }
    }
}
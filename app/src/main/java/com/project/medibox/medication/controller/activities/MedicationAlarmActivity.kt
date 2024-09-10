package com.project.medibox.medication.controller.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.medication.models.ApiAlarm
import com.project.medibox.medication.models.CompletedReminderAlarm
import com.project.medibox.medication.models.MissedReminderAlarm
import com.project.medibox.medication.network.MedicationApiService
import com.project.medibox.medication.persistence.UpcomingReminderAlarmDAO
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        val tvAlarmWithFood = findViewById<TextView>(R.id.tvAlarmWithFood)
        val upcomingAlarm = StateManager.selectedUpcomingAlarm
        val medicationApiService = SharedMethods.retrofitServiceBuilder(MedicationApiService::class.java)
        tvTimeForMedMedicine.text = upcomingAlarm.medicineName

        tvAlarmWithFood.text = when (upcomingAlarm.consumeFood) {
            true -> getString(R.string.with_food)
            false -> getString(R.string.without_food)
            null -> ""
        }

        btnMediAccept.setOnClickListener {
            AppDatabase.getInstance(this).getUpcomingReminderAlarmDao().deleteById(upcomingAlarm.id)
            val completedAlarm = CompletedReminderAlarm(
                0,
                upcomingAlarm.medicineName,
                upcomingAlarm.activateDateString,
                upcomingAlarm.activateHour,
                upcomingAlarm.activateMinute,
                upcomingAlarm.consumeFood
            )
            AppDatabase.getInstance(this).getCompletedReminderAlarmDao().insertAlarm(completedAlarm)
            val apiAlarm = SharedMethods.mapAlarmToCreateApiAlarmRes(completedAlarm, StateManager.loggedUserId)
            val request = medicationApiService.saveCompletedAlarm(StateManager.authToken, apiAlarm)
            request.enqueue(object : Callback<ApiAlarm> {
                override fun onResponse(p0: Call<ApiAlarm>, p1: Response<ApiAlarm>) {

                }

                override fun onFailure(p0: Call<ApiAlarm>, p1: Throwable) {
                    Toast.makeText(this@MedicationAlarmActivity,
                        getString(R.string.error_while_saving_the_alarm_in_cloud), Toast.LENGTH_SHORT).show()
                }

            })
            finish()
        }
        btnMediMissed.setOnClickListener {
            AppDatabase.getInstance(this).getUpcomingReminderAlarmDao().deleteById(upcomingAlarm.id)
            val missedAlarm = MissedReminderAlarm(
                0,
                upcomingAlarm.medicineName,
                upcomingAlarm.activateDateString,
                upcomingAlarm.activateHour,
                upcomingAlarm.activateMinute,
                upcomingAlarm.consumeFood
            )
            AppDatabase.getInstance(this).getMissedReminderAlarmDao().insertAlarm(missedAlarm)
            val apiAlarm = SharedMethods.mapAlarmToCreateApiAlarmRes(missedAlarm, StateManager.loggedUserId)
            val request = medicationApiService.saveMissedAlarm(StateManager.authToken, apiAlarm)
            request.enqueue(object : Callback<ApiAlarm> {
                override fun onResponse(p0: Call<ApiAlarm>, p1: Response<ApiAlarm>) {

                }

                override fun onFailure(p0: Call<ApiAlarm>, p1: Throwable) {
                    Toast.makeText(this@MedicationAlarmActivity,
                        getString(R.string.error_while_saving_the_alarm_in_cloud), Toast.LENGTH_SHORT).show()
                }

            })
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
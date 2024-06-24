package com.project.medibox.medication.controller.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.medibox.R
import com.project.medibox.medication.adapter.HistoricalReminderAdapter
import com.project.medibox.medication.models.HistoricalReminder
import com.project.medibox.medication.models.Reminder
import com.project.medibox.medication.network.MedicationApiService
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.OnItemClickListener
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class MedicationHistoryActivity : AppCompatActivity(), OnItemClickListener<HistoricalReminder> {
    private lateinit var rvMedHistory: RecyclerView
    private lateinit var reminderDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_medication_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadMedicationHistory()

    }
    private fun loadMedicationHistory() {
        rvMedHistory = findViewById(R.id.rvMedHistory)

        val reminderHistory = AppDatabase.getInstance(this).getHistoricalReminderDao().getAll()

        rvMedHistory.layoutManager = LinearLayoutManager(this)
        rvMedHistory.adapter = HistoricalReminderAdapter(reminderHistory, this)
    }

    override fun onItemClicked(value: HistoricalReminder) {
        val endDate = SharedMethods.convertDDMMYYYYToLocalDate(value.endDateStringSimply)
        if (endDate >= LocalDate.now()) {
            reminderDialog = Dialog(this)
            reminderDialog.setContentView(R.layout.dialog_reminder_options)
            reminderDialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            reminderDialog.window!!.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.bg_dialog_reminder_options))
            reminderDialog.setCancelable(true)
            val btnReminderEdit = reminderDialog.findViewById<Button>(R.id.btnReminderEdit)
            val btnReminderDelete = reminderDialog.findViewById<Button>(R.id.btnReminderDelete)

            btnReminderEdit.setOnClickListener {

                StateManager.selectedHistoricalReminder = value
                val intent = Intent(this, EditReminderActivity::class.java)
                startActivity(intent)
                reminderDialog.dismiss()
            }
            btnReminderDelete.setOnClickListener {
                deleteReminder(value)
                reminderDialog.dismiss()
            }
            reminderDialog.show()
        }
    }

    private fun deleteReminder(reminder: HistoricalReminder) {
        val medicationApiService = SharedMethods.retrofitServiceBuilder(MedicationApiService::class.java)
        val request = medicationApiService.deleteReminder(StateManager.authToken, reminder.reminderId)
        request.enqueue(object: Callback<Reminder> {
            override fun onResponse(call: Call<Reminder>, response: Response<Reminder>) {
                if (response.isSuccessful) {
                    AppDatabase.getInstance(this@MedicationHistoryActivity).getHistoricalReminderDao().deleteById(reminder.id)
                    AppDatabase.getInstance(this@MedicationHistoryActivity).getUpcomingReminderAlarmDao().deleteAllByReminderId(reminder.reminderId)
                    Toast.makeText(this@MedicationHistoryActivity, "Reminder deleted successfully.", Toast.LENGTH_SHORT).show()
                }
                else Toast.makeText(this@MedicationHistoryActivity, "Error while deleting reminder.", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(p0: Call<Reminder>, p1: Throwable) {
                Toast.makeText(this@MedicationHistoryActivity, "Error while deleting reminder.", Toast.LENGTH_SHORT).show()
            }

        })
    }
}
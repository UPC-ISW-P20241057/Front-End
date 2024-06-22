package com.project.medibox.medication.controller.activities

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
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
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.OnItemClickListener

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
        reminderDialog = Dialog(this)
        reminderDialog.setContentView(R.layout.dialog_reminder_options)
        reminderDialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        reminderDialog.window!!.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.bg_dialog_reminder_options))
        reminderDialog.setCancelable(true)
        val btnReminderEdit = reminderDialog.findViewById<Button>(R.id.btnReminderEdit)
        val btnReminderDelete = reminderDialog.findViewById<Button>(R.id.btnReminderDelete)

        btnReminderEdit.setOnClickListener {
            reminderDialog.dismiss()
        }
        btnReminderDelete.setOnClickListener {
            reminderDialog.dismiss()
        }
        reminderDialog.show()
    }
}
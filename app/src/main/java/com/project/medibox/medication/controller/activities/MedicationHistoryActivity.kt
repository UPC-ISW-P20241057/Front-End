package com.project.medibox.medication.controller.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
    }
}
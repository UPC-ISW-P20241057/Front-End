package com.project.medibox.medication.controller.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.shared.StateManager

class NextEditReminderActivity : AppCompatActivity() {
    private lateinit var cvEditInterval: CardView
    private lateinit var cvEditFrequency: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_next_edit_reminder)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        when(StateManager.selectedHistoricalReminder.type) {
            "Interval" -> showIntervalCard()
            "Frequency" -> showFrequencyCard()
        }
        val btnEditReminder = findViewById<Button>(R.id.btnEditReminder)
        btnEditReminder.setOnClickListener {
            editReminder()
        }
    }

    private fun editReminder() {

    }

    private fun showFrequencyCard() {
        cvEditFrequency = findViewById(R.id.cvEditFrequency)
        cvEditFrequency.visibility = View.VISIBLE
    }

    private fun showIntervalCard() {
        cvEditInterval = findViewById(R.id.cvEditInterval)
        cvEditInterval.visibility = View.VISIBLE
    }
}
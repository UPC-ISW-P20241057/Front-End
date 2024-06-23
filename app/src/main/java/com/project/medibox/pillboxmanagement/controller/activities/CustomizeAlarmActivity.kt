package com.project.medibox.pillboxmanagement.controller.activities

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.shared.AppDatabase

class CustomizeAlarmActivity : AppCompatActivity() {

    private lateinit var optionsSpinner: Spinner
    private lateinit var mediaPlayer: MediaPlayer
    private var selectedAlarm: Byte = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customize_alarm)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnCustAlarmSave = findViewById<Button>(R.id.btnCustAlarmSave)
        btnCustAlarmSave.setOnClickListener {
            saveChanges()
        }

        val btnCustAlarmCancel = findViewById<Button>(R.id.btnCustAlarmCancel)
        btnCustAlarmCancel.setOnClickListener {
            finish()
        }

        optionsSpinner = findViewById(R.id.optionsSpinner)

        val options = arrayOf("Select", "Alarm", "Twinkle Twinkle", "Fur Elise", "Nokia", "Star Wars")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        optionsSpinner.adapter = adapter

        optionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedAlarm = position.toByte()
                when (position) {
                    1 -> playTone(R.raw.alarm_tone)
                    2 -> playTone(R.raw.twinkle_twinkle_tone)
                    3 -> playTone(R.raw.fur_elise_tone)
                    4 -> playTone(R.raw.nokia_tone)
                    5 -> playTone(R.raw.star_wars_tone)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

    }

    private fun saveChanges() {
        if (selectedAlarm > 0) {
            AppDatabase.getInstance(this).getToneSettingsDao().changeTone(selectedAlarm)
            Toast.makeText(this, "Changed alarm successfully!", Toast.LENGTH_SHORT).show()
            Log.d("CustomizeAlarmActivity", AppDatabase.getInstance(this).getToneSettingsDao().getSettings().toString())
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) mediaPlayer.stop()
    }

    private fun playTone(resourceId: Int) {
        if (::mediaPlayer.isInitialized) mediaPlayer.stop()
        mediaPlayer = MediaPlayer.create(this, resourceId)
        mediaPlayer.start()
    }
}
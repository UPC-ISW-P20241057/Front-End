package com.project.medibox.medication.controller.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R

class NextNewScheduleActivity : AppCompatActivity() {
    private lateinit var swInternal: Switch
    private lateinit var spnTime: Spinner
    private lateinit var spnTimeType: Spinner

    private lateinit var swFrequency: Switch
    private lateinit var spnFreqTimes: Spinner
    private lateinit var spnPer: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_next_new_schedule)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        swInternal = findViewById(R.id.swInterval)
        spnTime = findViewById(R.id.spnTime)
        spnTimeType = findViewById(R.id.spnTimeType)
        disableInterval()

        swFrequency = findViewById(R.id.swFrequency)
        spnFreqTimes = findViewById(R.id.spnFrecTimes)
        spnPer = findViewById(R.id.spnPer)
        disableFrequency()

        loadSpinners()

        swInternal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableInterval()
                disableFrequency()
            }
            else {
                disableInterval()
            }
        }

        swFrequency.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableFrequency()
                disableInterval()
            }
            else {
                disableFrequency()
            }
        }
    }

    private fun enableInterval() {
        spnTime.isEnabled = true
        spnTimeType.isEnabled = true
    }
    private fun disableInterval() {
        spnTime.isEnabled = false
        spnTimeType.isEnabled = false
        swInternal.isChecked = false
    }

    private fun enableFrequency() {
        spnFreqTimes.isEnabled = true
        spnPer.isEnabled = true
    }

    private fun disableFrequency() {
        spnFreqTimes.isEnabled = false
        spnPer.isEnabled = false
        swFrequency.isChecked = false
    }

    private fun loadSpinners() {
        val timeOptions = (1..24).map { it.toString() }
        val timeTypeOptions = listOf("Hour(s)", "Day(s)")
        val freqTimesOptions = (1..30).map { it.toString() }
        val spnPerOptions = listOf("Day", "Week")

        val timeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeOptions)
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeTypeOptions)
        val freqTimesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, freqTimesOptions)
        val spnPerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spnPerOptions)

        spnTime.adapter = timeAdapter
        spnTimeType.adapter = typeAdapter
        spnFreqTimes.adapter = freqTimesAdapter
        spnPer.adapter = spnPerAdapter

        spnTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        spnTimeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        spnFreqTimes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        spnPer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }
}
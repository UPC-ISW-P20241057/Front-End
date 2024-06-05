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
import com.google.android.material.switchmaterial.SwitchMaterial
import com.project.medibox.R

class NextNewScheduleActivity : AppCompatActivity() {
    private lateinit var spnTime: Spinner
    private lateinit var spnTimeType: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_next_new_schedule)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val swInternal = findViewById<Switch>(R.id.swInterval)
        spnTime = findViewById(R.id.spnTime)
        spnTimeType = findViewById(R.id.spnTimeType)
        disableInterval()

        loadSpinners()

        swInternal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableInterval()
            }
            else {
                disableInterval()
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
    }

    private fun loadSpinners() {
        val timeOptions = (1..24).map { it.toString() }
        val timeTypeOptions = listOf("Hour(s)", "Day(s)")

        val timeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeOptions)
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeTypeOptions)

        spnTime.adapter = timeAdapter
        spnTimeType.adapter = typeAdapter

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
    }
}
package com.project.medibox.controllers.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R

class CustomizeAlarmActivity : AppCompatActivity() {

    private lateinit var optionsSpinner: Spinner
    private lateinit var selectedOptionTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customize_alarm)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        optionsSpinner = findViewById(R.id.optionsSpinner)
        selectedOptionTextView = findViewById(R.id.selectedCategoryTextView)

        val options = arrayOf("Alarm 1", "Alarm 2", "Alarm 3", "Alarm 4")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        optionsSpinner.adapter = adapter

        optionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position) as String
                selectedOptionTextView.text = selectedCategory
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No se seleccionó nada
            }
        }

    }
}
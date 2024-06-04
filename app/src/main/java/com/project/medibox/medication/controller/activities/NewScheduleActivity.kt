package com.project.medibox.medication.controller.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.medication.models.Medicine
import com.project.medibox.medication.network.MedicationService
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import com.project.medibox.shared.StateManager.selectedMedicine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewScheduleActivity : AppCompatActivity() {
    private lateinit var optionsSpinner: Spinner



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_schedule)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        optionsSpinner = findViewById(R.id.osMedicines)
        val medicationService = SharedMethods.retrofitServiceBuilder(MedicationService::class.java)
        val request = medicationService.getAllMedicines(StateManager.authToken)
        request.enqueue(object : Callback<List<Medicine>> {
            override fun onResponse(call: Call<List<Medicine>>, response: Response<List<Medicine>>) {
                if (response.isSuccessful) {

                    loadSpinner(response.body()!!)
                }
                else Toast.makeText(this@NewScheduleActivity, "Error al obtener medicinas.", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<List<Medicine>>, t: Throwable) {
                Toast.makeText(this@NewScheduleActivity, "Error al obtener medicinas.1", Toast.LENGTH_SHORT).show()
            }

        })


    }
    private fun loadSpinner(medicines: List<Medicine>) {
        val options = medicines.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        optionsSpinner.adapter = adapter

        optionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMedicineStr = parent.getItemAtPosition(position) as String
                selectedMedicine = medicines.first {it.name == selectedMedicineStr}
                //medicines.indexOfFirst { it.name == selectedMedicineStr }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    fun goToNextActivity(view: View) {
        if (selectedMedicine != null) {
            val intent = Intent(this, NextNewScheduleActivity::class.java)
            startActivity(intent)
            finish()
        }
        else Toast.makeText(this, "Please select a medicine.", Toast.LENGTH_SHORT).show()
    }
}
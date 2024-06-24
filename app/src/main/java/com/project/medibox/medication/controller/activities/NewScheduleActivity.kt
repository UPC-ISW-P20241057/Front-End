package com.project.medibox.medication.controller.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.medication.dialogs.ConflictingMedicineDialog
import com.project.medibox.medication.models.ConflictingMedicines
import com.project.medibox.medication.models.Medicine
import com.project.medibox.medication.network.ConflictingMedicinesQuery
import com.project.medibox.medication.network.MedicationApiService
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import com.project.medibox.shared.StateManager.selectedMedicine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.util.Timer

class NewScheduleActivity : AppCompatActivity() {
    private lateinit var optionsSpinner: Spinner
    private lateinit var medicines: List<String>
    private lateinit var medicationApiService: MedicationApiService


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
        val medicationApiService =
            SharedMethods.retrofitServiceBuilder(MedicationApiService::class.java)
        val request = medicationApiService.getAllMedicines(StateManager.authToken)
        request.enqueue(object : Callback<List<Medicine>> {
            override fun onResponse(
                call: Call<List<Medicine>>,
                response: Response<List<Medicine>>
            ) {
                if (response.isSuccessful) {

                    loadSpinner(response.body()!!)
                } else Toast.makeText(
                    this@NewScheduleActivity,
                    "Error al obtener medicinas.",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onFailure(call: Call<List<Medicine>>, t: Throwable) {
                Toast.makeText(
                    this@NewScheduleActivity,
                    "Error al obtener medicinas.1",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

        val btnSchNext = findViewById<Button>(R.id.btnSchNext)
        btnSchNext.setOnClickListener {
            checkConflictingMedicine()
        }
    }

    private fun loadSpinner(medicines: List<Medicine>) {
        optionsSpinner.visibility = View.VISIBLE
        val options = medicines.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        optionsSpinner.adapter = adapter

        optionsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedMedicineStr = parent.getItemAtPosition(position) as String
                selectedMedicine = medicines.first { it.name == selectedMedicineStr }
                //medicines.indexOfFirst { it.name == selectedMedicineStr }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    fun goToNextActivity() {
        val intent = Intent(this, NextNewScheduleActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkConflictingMedicine() {
        if (selectedMedicine != null) {
            val reminders =
                AppDatabase.getInstance(this@NewScheduleActivity).getHistoricalReminderDao()
                    .getAll()
            val medicines = reminders.flatMap { listOf(it.medicineName) }.distinct()

            if (!medicines.contains(selectedMedicine!!.name)) {
                var conflictingFound = false
                val conflictingMedicineDialogTag = "CONFLICTING_MEDICINE_DIALOG"

                for (medicine in medicines) {
                    val result = runBlocking {isMedicineConflicting(medicine)}
                    if (result != null) {
                        conflictingFound = true
                        ConflictingMedicineDialog(medicine, selectedMedicine!!.name).show(
                            supportFragmentManager,
                            conflictingMedicineDialogTag
                        )
                        break
                    } else {
                        Log.d(
                            "CONFLICTING",
                            "Medicine $medicine not conflicting with ${selectedMedicine!!.name}"
                        )
                    }
                }

                if (!conflictingFound) {
                    goToNextActivity()
                }
            } else {
                goToNextActivity()
            }
        } else {
            Toast.makeText(
                this@NewScheduleActivity,
                "Please select a medicine.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private suspend fun isMedicineConflicting(medicine: String): ConflictingMedicines? {
        return withContext(Dispatchers.IO) {
            try {
                medicationApiService = SharedMethods.retrofitServiceBuilder(MedicationApiService::class.java)
                val request = medicationApiService.verifyConflictingMedicines(
                    StateManager.authToken, ConflictingMedicinesQuery(
                        selectedMedicine!!.name,
                        medicine
                    )
                )

                val response = request.execute()

                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                // Maneja el error aquí
                null
            }
        }
    }

}
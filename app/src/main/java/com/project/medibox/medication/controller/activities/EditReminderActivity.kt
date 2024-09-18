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
import com.project.medibox.medication.network.MedicationApiService
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import com.project.medibox.shared.StateManager.selectedMedicine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditReminderActivity : AppCompatActivity() {

    private lateinit var optionsSpinner: Spinner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_schedule)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        optionsSpinner = findViewById(R.id.spnEditMedicines)
        val medicationApiService = SharedMethods.retrofitServiceBuilder(MedicationApiService::class.java)
        val request = medicationApiService.getAllMedicines(StateManager.authToken)
        request.enqueue(object : Callback<List<Medicine>> {
            override fun onResponse(call: Call<List<Medicine>>, response: Response<List<Medicine>>) {
                if (response.isSuccessful) {

                    loadSpinner(response.body()!!)
                }
                else Toast.makeText(this@EditReminderActivity,
                    getString(R.string.error_while_getting_medicines), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<List<Medicine>>, t: Throwable) {
                Toast.makeText(this@EditReminderActivity, getString(R.string.error_while_getting_medicines), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun loadSpinner(medicines: List<Medicine>) {
        optionsSpinner.visibility = View.VISIBLE
        val options = medicines.map { it.name }.sorted().toMutableList()
        if (options.contains(getString(R.string.other))) {
            options.remove(getString(R.string.other))
        }
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

    fun goToNextEditActivity(view: View) {
        if (selectedMedicine != null) {
            val intent = Intent(this, NextEditReminderActivity::class.java)
            startActivity(intent)
            finish()
        }
        else Toast.makeText(this, getString(R.string.please_select_a_medicine), Toast.LENGTH_SHORT).show()
    }
}
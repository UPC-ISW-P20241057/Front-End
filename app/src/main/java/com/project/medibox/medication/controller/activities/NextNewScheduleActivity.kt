package com.project.medibox.medication.controller.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.medication.models.Frequency
import com.project.medibox.medication.models.Interval
import com.project.medibox.medication.models.Reminder
import com.project.medibox.medication.network.MedicationService
import com.project.medibox.medication.resources.CreateFrequencyResource
import com.project.medibox.medication.resources.CreateIntervalResource
import com.project.medibox.medication.resources.CreateReminderResource
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime

class NextNewScheduleActivity : AppCompatActivity() {
    private lateinit var swInterval: Switch
    private lateinit var spnIntervalTime: Spinner
    private lateinit var spnIntervalTimeType: Spinner

    private lateinit var swFrequency: Switch
    private lateinit var spnFreqTimes: Spinner
    private lateinit var spnPer: Spinner

    private lateinit var swPills: Switch
    private lateinit var etPills: EditText

    private lateinit var spnForTime: Spinner
    private lateinit var spnForTimeType: Spinner

    private var interval = CreateIntervalResource("", -1, -1)
    private var frequency = CreateFrequencyResource("", -1, -1)

    private lateinit var swLapse: Switch
    private var lapseTime: Int = -1
    private var lapseType: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_next_new_schedule)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        swInterval = findViewById(R.id.swInterval)
        spnIntervalTime = findViewById(R.id.spnIntervalTime)
        spnIntervalTimeType = findViewById(R.id.spnIntervalTimeType)
        disableInterval()

        swFrequency = findViewById(R.id.swFrequency)
        spnFreqTimes = findViewById(R.id.spnFrecTimes)
        spnPer = findViewById(R.id.spnPer)
        disableFrequency()

        swPills = findViewById(R.id.swPills)

        etPills = findViewById(R.id.etPills)
        disablePillQuantity()

        swLapse = findViewById(R.id.swLapse)
        spnForTime = findViewById(R.id.spnForTime)
        spnForTimeType = findViewById(R.id.spnForTimeType)
        disableLapse()

        loadSpinners()

        swInterval.setOnCheckedChangeListener { _, isChecked ->
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

        swPills.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enablePillQuantity()
            }
            else {
                disablePillQuantity()
            }
        }

        swLapse.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                enableLapse()
            }
            else {
                disableLapse()
            }
        }
    }

    private fun enableLapse() {
        spnForTime.isEnabled = true
        spnForTimeType.isEnabled = true
    }

    private fun disableLapse() {
        spnForTime.isEnabled = false
        spnForTimeType.isEnabled = false
    }

    private fun disablePillQuantity() {
        etPills.isEnabled = false
    }

    private fun enablePillQuantity() {
        etPills.isEnabled = true
    }

    private fun enableInterval() {
        spnIntervalTime.isEnabled = true
        spnIntervalTimeType.isEnabled = true
    }
    private fun disableInterval() {
        spnIntervalTime.isEnabled = false
        spnIntervalTimeType.isEnabled = false
        swInterval.isChecked = false
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
        val intervalTimeOptions = (1..24).map { it.toString() }
        val intervalTimeTypeOptions = listOf("Hour(s)", "Day(s)")
        val freqTimesOptions = (1..30).map { it.toString() }
        val spnPerOptions = listOf("Day", "Week")
        val spnForTimeOptions = (1..30).map { it.toString() }
        val spnForTimeTypeOptions = listOf("Days", "Week(s)")

        val intervalTimeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, intervalTimeOptions)
        val intervalTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, intervalTimeTypeOptions)
        val freqTimesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, freqTimesOptions)
        val spnPerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spnPerOptions)
        val spnForTimeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spnForTimeOptions)
        val spnForTimeTypeAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_item, spnForTimeTypeOptions)

        spnIntervalTime.adapter = intervalTimeAdapter
        spnIntervalTimeType.adapter = intervalTypeAdapter
        spnFreqTimes.adapter = freqTimesAdapter
        spnPer.adapter = spnPerAdapter
        spnForTime.adapter = spnForTimeAdapter
        spnForTimeType.adapter = spnForTimeTypeAdapter

        spnIntervalTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                interval.quantity = parent.getItemAtPosition(position) as Int
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                interval.quantity = -1
            }

        }

        spnIntervalTimeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                interval.type = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                interval.type = ""
            }

        }

        spnFreqTimes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                frequency.times = parent.getItemAtPosition(position) as Int
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                frequency.times = -1
            }

        }

        spnPer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                frequency.type = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                frequency.type = ""
            }

        }

        spnForTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                lapseTime = parent.getItemAtPosition(position) as Int
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                lapseTime = -1
            }

        }
        spnForTimeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                lapseType = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                lapseType = ""
            }

        }
    }
    fun createSchedule(view: View) {
        val etPills = findViewById<EditText>(R.id.etPills)
        val rgrpFood = findViewById<RadioGroup>(R.id.rgrpFood)
        val medicationService = SharedMethods.retrofitServiceBuilder(MedicationService::class.java)
        val now = LocalDateTime.now()

        var endDateString: String? = null

        if (swLapse.isChecked) {
            endDateString = when(lapseType) {
                "Days" -> SharedMethods.getJSDate(now.plusDays(lapseTime.toLong()))
                "Weeks" -> SharedMethods.getJSDate(now.plusWeeks(lapseTime.toLong()))
                else -> null
            }
        }

        val selectedFoodRadio = findViewById<RadioButton>(rgrpFood.checkedRadioButtonId)
        val foodOption = selectedFoodRadio.text.toString()
        val consumedFood: Boolean? = when(foodOption) {
            "Yes" -> true
            "No" -> false
            "It doesn't matter" -> null
            else -> null
        }



        val postReminderRequest = medicationService.createReminder(StateManager.authToken, CreateReminderResource(
            SharedMethods.getJSDate(now),
            etPills.text.toString().toInt(),
            endDateString,
            StateManager.selectedMedicine!!.id,
            StateManager.loggedUserId,
            consumedFood
        ))

        postReminderRequest.enqueue(object : Callback<Reminder> {
            override fun onResponse(call: Call<Reminder>, response: Response<Reminder>) {
                if (response.isSuccessful) {
                    val reminderId = response.body()!!.id
                    if (swInterval.isChecked) {
                        val postIntervalRequest = medicationService.createInterval(StateManager.authToken, CreateIntervalResource(
                            spnIntervalTimeType.selectedItem.toString(),
                            spnIntervalTime.selectedItem.toString().toInt(),
                            reminderId
                        ))
                        postIntervalRequest.enqueue(object : Callback<Interval> {
                            override fun onResponse(p0: Call<Interval>, p1: Response<Interval>) {
                                if (response.isSuccessful)
                                    Toast.makeText(this@NextNewScheduleActivity, "Reminder with interval created successfully", Toast.LENGTH_SHORT).show()
                                else {
                                    Toast.makeText(this@NextNewScheduleActivity, "Error while creating interval of reminder. Destroying corrupt reminder...", Toast.LENGTH_SHORT).show()
                                    medicationService.deleteReminder(StateManager.authToken, reminderId)
                                }
                            }

                            override fun onFailure(p0: Call<Interval>, p1: Throwable) {
                                Toast.makeText(this@NextNewScheduleActivity, "Error while creating interval of reminder. Destroying corrupt reminder...", Toast.LENGTH_SHORT).show()
                                medicationService.deleteReminder(StateManager.authToken, reminderId)
                            }

                        })
                    }
                    else if (swFrequency.isChecked) {
                        val postFrequencyRequest = medicationService.createFrequency(StateManager.authToken, CreateFrequencyResource(
                            spnFreqTimes.selectedItem.toString(),
                            spnPer.selectedItem.toString().toInt(),
                            reminderId
                        ))
                        postFrequencyRequest.enqueue(object : Callback<Frequency> {
                            override fun onResponse(p0: Call<Frequency>, p1: Response<Frequency>) {
                                if (response.isSuccessful)
                                    Toast.makeText(this@NextNewScheduleActivity, "Reminder with interval created successfully", Toast.LENGTH_SHORT).show()
                                else {
                                    Toast.makeText(this@NextNewScheduleActivity, "Error while creating frequency of reminder. Destroying corrupt reminder...", Toast.LENGTH_SHORT).show()
                                    medicationService.deleteReminder(StateManager.authToken, reminderId)
                                }
                            }

                            override fun onFailure(p0: Call<Frequency>, p1: Throwable) {
                                Toast.makeText(this@NextNewScheduleActivity, "Error while creating frequency of reminder. Destroying corrupt reminder...", Toast.LENGTH_SHORT).show()
                                medicationService.deleteReminder(StateManager.authToken, reminderId)
                            }

                        })
                    }
                }
                else Toast.makeText(this@NextNewScheduleActivity, "Error while creating reminder", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(p0: Call<Reminder>, p1: Throwable) {
                Toast.makeText(this@NextNewScheduleActivity, "Error while creating reminder", Toast.LENGTH_SHORT).show()
            }

        })
    }
}
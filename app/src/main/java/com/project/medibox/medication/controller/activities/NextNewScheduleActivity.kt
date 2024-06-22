package com.project.medibox.medication.controller.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.project.medibox.R
import com.project.medibox.medication.models.Frequency
import com.project.medibox.medication.models.HistoricalReminder
import com.project.medibox.medication.models.Interval
import com.project.medibox.medication.models.Reminder
import com.project.medibox.medication.network.MedicationApiService
import com.project.medibox.medication.resources.CreateFrequencyResource
import com.project.medibox.medication.resources.CreateIntervalResource
import com.project.medibox.medication.resources.CreateReminderResource
import com.project.medibox.medication.services.ReminderService
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.LocalTime

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

    private lateinit var timePicker: MaterialTimePicker


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

        val btnCreateSchedule = findViewById<Button>(R.id.btnCreateSchedule)
        btnCreateSchedule.setOnClickListener {
            createSchedule()
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

    private fun setIntervalTimeSpinner(type: String) {
        val intervalTimeOptions: List<String> = if (type == "Hours") {
            listOf("6", "8", "12")
        } else {
            (1..3).map { it.toString() }
        }

        val intervalTimeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, intervalTimeOptions)
        spnIntervalTime.adapter = intervalTimeAdapter
        spnIntervalTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                interval.quantity = parent.getItemAtPosition(position).toString().toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                interval.quantity = -1
            }

        }
    }

    private fun loadSpinners() {

        val intervalTimeTypeOptions = listOf("Hours", "Days")
        val freqTimesOptions = (1..2).map { it.toString() }
        val spnPerOptions = listOf("Day")
        val spnForTimeOptions = (1..30).map { it.toString() }
        val spnForTimeTypeOptions = listOf("Days", "Weeks")


        val intervalTypeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, intervalTimeTypeOptions)
        val freqTimesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, freqTimesOptions)
        val spnPerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spnPerOptions)
        val spnForTimeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spnForTimeOptions)
        val spnForTimeTypeAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_item, spnForTimeTypeOptions)


        spnIntervalTimeType.adapter = intervalTypeAdapter
        spnFreqTimes.adapter = freqTimesAdapter
        spnPer.adapter = spnPerAdapter
        spnForTime.adapter = spnForTimeAdapter
        spnForTimeType.adapter = spnForTimeTypeAdapter

        spnIntervalTimeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                interval.type = parent.getItemAtPosition(position) as String
                setIntervalTimeSpinner(parent.getItemAtPosition(position) as String)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                interval.type = ""
            }

        }

        spnFreqTimes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                frequency.times = parent.getItemAtPosition(position).toString().toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                frequency.times = -1
            }

        }

        spnPer.isEnabled = false
        frequency.type = "Days"

        spnForTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                lapseTime = parent.getItemAtPosition(position).toString().toInt()
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
    private fun makeHttpRequest(pills: Int?, startDate: LocalDateTime, endDateString: String?, consumedFood: Boolean?) {
        val medicationApiService = SharedMethods.retrofitServiceBuilder(MedicationApiService::class.java)
        val postReminderRequest = medicationApiService.createReminder(StateManager.authToken, CreateReminderResource(
            SharedMethods.getJSDateFromLocalDateTime(startDate),
            pills,
            endDateString,
            StateManager.selectedMedicine!!.id,
            StateManager.loggedUserId,
            consumedFood
        ))

        postReminderRequest.enqueue(object : Callback<Reminder> {
            override fun onResponse(call: Call<Reminder>, reminderResponse: Response<Reminder>) {
                if (reminderResponse.isSuccessful) {
                    val reminder = reminderResponse.body()!!
                    if (swInterval.isChecked) {
                        interval.reminderId = reminder.id
                        val postIntervalRequest = medicationApiService.createInterval(StateManager.authToken, interval)
                        postIntervalRequest.enqueue(object : Callback<Interval> {
                            override fun onResponse(call: Call<Interval>, response: Response<Interval>) {
                                if (response.isSuccessful) {
                                    ReminderService.createAlarms(this@NextNewScheduleActivity, reminder, response.body()!!)
                                    saveHistoricalReminder(reminder, "Interval")
                                    Toast.makeText(this@NextNewScheduleActivity, "Reminder with interval created successfully", Toast.LENGTH_SHORT).show()
                                }

                                else {
                                    Toast.makeText(this@NextNewScheduleActivity, "Error while creating interval of reminder. Destroying corrupt reminder...", Toast.LENGTH_SHORT).show()
                                    medicationApiService.deleteReminder(StateManager.authToken, reminder.id)
                                }
                            }

                            override fun onFailure(p0: Call<Interval>, p1: Throwable) {
                                Toast.makeText(this@NextNewScheduleActivity, "Error while creating interval of reminder. Destroying corrupt reminder...", Toast.LENGTH_SHORT).show()
                                medicationApiService.deleteReminder(StateManager.authToken, reminder.id)
                            }

                        })
                    }
                    else if (swFrequency.isChecked) {
                        frequency.reminderId = reminder.id
                        val postFrequencyRequest = medicationApiService.createFrequency(StateManager.authToken, frequency)
                        postFrequencyRequest.enqueue(object : Callback<Frequency> {
                            override fun onResponse(call: Call<Frequency>, response: Response<Frequency>) {
                                if (response.isSuccessful) {
                                    ReminderService.createAlarms(this@NextNewScheduleActivity, reminder, response.body()!!)
                                    saveHistoricalReminder(reminder, "Frequency")
                                    Toast.makeText(this@NextNewScheduleActivity, "Reminder with frequency created successfully", Toast.LENGTH_SHORT).show()
                                }
                                else {
                                    Toast.makeText(this@NextNewScheduleActivity, "Error while creating frequency of reminder. Destroying corrupt reminder...", Toast.LENGTH_SHORT).show()
                                    medicationApiService.deleteReminder(StateManager.authToken, reminder.id)
                                }
                            }

                            override fun onFailure(p0: Call<Frequency>, p1: Throwable) {
                                Toast.makeText(this@NextNewScheduleActivity, "Error while creating frequency of reminder. Destroying corrupt reminder...", Toast.LENGTH_SHORT).show()
                                medicationApiService.deleteReminder(StateManager.authToken, reminder.id)
                            }

                        })
                    }
                    finish()
                }
                else Toast.makeText(this@NextNewScheduleActivity, "Error while creating reminder", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(p0: Call<Reminder>, p1: Throwable) {
                Toast.makeText(this@NextNewScheduleActivity, "Error while creating reminder", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun saveHistoricalReminder(reminder: Reminder, type: String) {
        val createdDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.createdDateString)
        val createdDateParsed = SharedMethods.getDDMMYYStringFromDate(createdDate)
        val endDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.endDateString!!)
        val endDateParsed = SharedMethods.getDDMMYYStringFromDate(endDate)
        AppDatabase.getInstance(this).getHistoricalReminderDao().insertReminder(HistoricalReminder(
            0,
            createdDateParsed,
            reminder.pills,
            endDateParsed,
            StateManager.selectedMedicine!!.name,
            type,
            reminder.consumeFood,
            reminder.id
        ))
    }

    private fun createSchedule() {
        val now = LocalDateTime.now()

        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select reminder start time")
            .build()


        val etPills = findViewById<EditText>(R.id.etPills)
        val rgrpFood = findViewById<RadioGroup>(R.id.rgrpFood)


        val selectedFoodRadio = findViewById<RadioButton>(rgrpFood.checkedRadioButtonId)
        val foodOption = selectedFoodRadio.text.toString()
        val consumedFood: Boolean? = when(foodOption) {
            "Yes" -> true
            "No" -> false
            "It doesn't matter" -> null
            else -> null
        }

        val pills: Int? = when(swPills.isChecked) {
            true -> etPills.text.toString().toInt()
            false -> null
        }

        if ((swInterval.isChecked && (spnIntervalTime.selectedItem.toString() == "6" || spnIntervalTime.selectedItem.toString() == "8") && spnIntervalTimeType.selectedItem.toString() == "Hours") ||
            swFrequency.isChecked && spnFreqTimes.selectedItem.toString() == "2" && spnPer.selectedItem.toString() == "Day") {
            val createdDate = now.plusDays(1)
            val endDateString: String? = when(lapseType) {
                "Days" -> SharedMethods.getJSDateFromLocalDateTime(createdDate.plusDays(lapseTime.toLong()))
                "Weeks" -> SharedMethods.getJSDateFromLocalDateTime(createdDate.plusWeeks(lapseTime.toLong()))
                else -> null
            }
            makeHttpRequest(pills, createdDate, endDateString, consumedFood)
        }
        else if ((swInterval.isChecked && spnIntervalTime.selectedItem.toString() == "12" && spnIntervalTimeType.selectedItem.toString() == "Hours") ||
            (swInterval.isChecked && spnIntervalTimeType.selectedItem.toString() == "Days") ||
            (swFrequency.isChecked && spnFreqTimes.selectedItem.toString() == "1" && spnPer.selectedItem.toString() == "Day")) {


            timePicker.show(supportFragmentManager, "Reminder time")
            timePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this@NextNewScheduleActivity, "You need to indicate the reminder start time.", Toast.LENGTH_SHORT).show()
            }
            timePicker.addOnPositiveButtonClickListener {
                val nowHour = LocalTime.of(now.hour, now.minute)
                val createHour = LocalTime.of(timePicker.hour, timePicker.minute)
                var createdDate = LocalDateTime.of(now.toLocalDate(), createHour)
                if (nowHour >= createHour) {
                    createdDate = createdDate.plusDays(1)
                }
                val endDateString: String? = when(lapseType) {
                    "Days" -> SharedMethods.getJSDateFromLocalDateTime(createdDate.plusDays(lapseTime.toLong()))
                    "Weeks" -> SharedMethods.getJSDateFromLocalDateTime(createdDate.plusWeeks(lapseTime.toLong()))
                    else -> null
                }
                makeHttpRequest(pills, createdDate, endDateString, consumedFood)
            }
        }

    }
}
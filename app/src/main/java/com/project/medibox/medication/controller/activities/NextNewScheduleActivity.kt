package com.project.medibox.medication.controller.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.project.medibox.R
import com.project.medibox.medication.models.Frequency
import com.project.medibox.medication.models.HistoricalReminder
import com.project.medibox.medication.models.Interval
import com.project.medibox.medication.models.MedicineImage
import com.project.medibox.medication.models.Reminder
import com.project.medibox.medication.network.MedicationApiService
import com.project.medibox.medication.persistence.MedicineImageDAO
import com.project.medibox.medication.resources.CreateFrequencyResource
import com.project.medibox.medication.resources.CreateIntervalResource
import com.project.medibox.medication.resources.CreateReminderResource
import com.project.medibox.medication.services.ReminderService
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.BitmapConverter
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.random.Random

class NextNewScheduleActivity : AppCompatActivity() {
    private lateinit var swInterval: Switch
    private lateinit var spnIntervalTime: Spinner
    private lateinit var spnIntervalTimeType: Spinner

    private lateinit var swFrequency: Switch
    private lateinit var spnFreqTimes: Spinner
    private lateinit var spnPer: Spinner

    private lateinit var spnForTime: Spinner
    private lateinit var spnForTimeType: Spinner

    private var existingImage: MedicineImage? = null

    private var interval = CreateIntervalResource("", -1, -1)
    private var frequency = CreateFrequencyResource("", -1, -1)

    private var lapseTime: Int = -1
    private var lapseType: String = ""

    private lateinit var timePicker: MaterialTimePicker

    private lateinit var imageDao: MedicineImageDAO
    private var bitmap: Bitmap? = null

    private var localId: Short = -1

    private var uri: Uri? = null
    private val REQUEST_IMAGE_CAPTURE = 1 // Puedes usar cualquier número como código de solicitud


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_next_new_schedule)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ivReminderInstructions = findViewById<ImageView>(R.id.ivReminderInstructions)

        imageDao = AppDatabase.getInstance(this).getMedicineImageDao()

        swInterval = findViewById(R.id.swInterval)
        spnIntervalTime = findViewById(R.id.spnIntervalTime)
        spnIntervalTimeType = findViewById(R.id.spnIntervalTimeType)
        disableInterval()

        swFrequency = findViewById(R.id.swFrequency)
        spnFreqTimes = findViewById(R.id.spnFrecTimes)
        spnPer = findViewById(R.id.spnPer)
        disableFrequency()

        spnForTime = findViewById(R.id.spnForTime)
        spnForTimeType = findViewById(R.id.spnForTimeType)

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


        val btnCreateSchedule = findViewById<Button>(R.id.btnCreateSchedule)
        btnCreateSchedule.setOnClickListener {
            createSchedule()
        }

        val cvUploadPhoto = findViewById<CardView>(R.id.cvUploadPhoto)
        cvUploadPhoto.setOnClickListener {
            requestImagePermission()
        }

        val cvTakePhoto = findViewById<CardView>(R.id.cvTakePhoto)
        cvTakePhoto.setOnClickListener {
            requestCameraPermission()
        }

        if (SharedMethods.isDarkTheme(this)) {
            val rbtnFoodYes = findViewById<RadioButton>(R.id.rbtnFoodYes)
            val rbtnFoodNo = findViewById<RadioButton>(R.id.rbtnFoodNo)
            val rbtnFoodNotMatter = findViewById<RadioButton>(R.id.rbtnFoodNotMatter)

            rbtnFoodYes.setTextColor(Color.WHITE)
            rbtnFoodNo.setTextColor(Color.WHITE)
            rbtnFoodNotMatter.setTextColor(Color.WHITE)

            ivReminderInstructions.setImageResource(R.mipmap.instructions_white)
        }

        ivReminderInstructions.setOnClickListener {
            showReminderInstructionsDialog()
        }
    }

    private fun showReminderInstructionsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.how_alarms_are_programmed))
            .setMessage(getString(R.string.alarm_info))
            .setPositiveButton(getString(R.string.accept)) { _, _ ->

            }
        val dialog = builder.create()
        dialog.show()
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
        val intervalTimeOptions: List<String> = if (type == getString(R.string.hours)) {
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

    private val requestGalleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageFromGallery()
        }
        else {
            Toast.makeText(this, getString(R.string.you_need_permission_to_select_image), Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestImagePermission() {
        val permission =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
                Manifest.permission.READ_EXTERNAL_STORAGE
            else
                Manifest.permission.READ_MEDIA_IMAGES
        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickImageFromGallery()
            }
            else -> {
                requestGalleryPermissionLauncher.launch(permission)
            }
        }
    }

    private fun saveMedicineImage() {
        val imageString = BitmapConverter.converterBitmapToString(bitmap!!)
        val medicineName = if (StateManager.selectedMedicine!!.name == getString(R.string.other)) StateManager.customMedicine else StateManager.selectedMedicine!!.name
        existingImage = imageDao.getImageByMedicineName(medicineName)
        if (existingImage == null)
            imageDao.insertImage(MedicineImage(
                0,
                imageString,
                medicineName
            ))
        else
            imageDao.updateImage(MedicineImage(
                existingImage!!.id,
                imageString,
                medicineName
            ))
    }
    private val startForActivityGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data)
            saveMedicineImage()
        }
    }
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startForActivityGallery.launch(intent)
    }

    private fun requestCameraPermission() {
        val permission = Manifest.permission.CAMERA

        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestCameraPermissionLauncher.launch(permission)
            }
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        }
        else {
            Toast.makeText(this,
                getString(R.string.you_need_permission_to_take_photos), Toast.LENGTH_SHORT).show()
        }
    }
    private val startForActivityCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Obtén la imagen capturada desde la cámara
            bitmap = result.data?.extras?.get("data") as? Bitmap
            if (bitmap != null) {
                // Guarda la imagen en el almacenamiento interno
                saveMedicineImage()
            }
        } else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startForActivityCamera.launch(intent)
    }

    private fun loadSpinners() {

        val intervalTimeTypeOptions = listOf(getString(R.string.hours), getString(R.string.days))
        val freqTimesOptions = (1..2).map { it.toString() }
        val spnPerOptions = listOf(getString(R.string.day))
        val spnForTimeOptions = (1..30).map { it.toString() }
        val spnForTimeTypeOptions = listOf(getString(R.string.days), getString(R.string.weeks))


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
        frequency.type = getString(R.string.days)

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

    private fun saveReminderWithCustomMedicine(startDate: LocalDateTime, endDateString: String?, consumedFood: Boolean?) {
        val reminder = Reminder(
            0,
            SharedMethods.getJSDateFromLocalDateTime(startDate),
            null,
            endDateString,
            StateManager.selectedMedicine!!,
            StateManager.loggedUserId,
            null,
            null,
            consumedFood
        )
        if (swInterval.isChecked) {
            val procIntr = Interval(0, interval.type, interval.quantity)
            reminder.interval = procIntr
            saveHistoricalReminder(reminder, "Interval", 0, StateManager.customMedicine)
            ReminderService.createAlarms(this, reminder, procIntr, StateManager.customMedicine, localId)
            Toast.makeText(this, getString(R.string.local_reminder_created), Toast.LENGTH_SHORT).show()
            finish()
        }
        else if (swFrequency.isChecked) {
            val procFreq = Frequency(0, frequency.type, frequency.times)
            reminder.frequency = procFreq
            saveHistoricalReminder(reminder, "Frequency", 0, StateManager.customMedicine)
            ReminderService.createAlarms(this, reminder, procFreq, StateManager.customMedicine, localId)
            Toast.makeText(this, getString(R.string.local_reminder_created), Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    private fun makeHttpRequest(startDate: LocalDateTime, endDateString: String?, consumedFood: Boolean?) {
        val medicationApiService = SharedMethods.retrofitServiceBuilder(MedicationApiService::class.java)
        val postReminderRequest = medicationApiService.createReminder(StateManager.authToken, CreateReminderResource(
            SharedMethods.getJSDateFromLocalDateTime(startDate),
            null,
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
                                    saveHistoricalReminder(reminder, "Interval", response.body()!!.id)
                                    Toast.makeText(this@NextNewScheduleActivity,
                                        getString(R.string.reminder_created_successfully), Toast.LENGTH_SHORT).show()
                                }

                                else {
                                    Toast.makeText(this@NextNewScheduleActivity, getString(R.string.error_while_creating_interval_of_reminder), Toast.LENGTH_SHORT).show()
                                    medicationApiService.deleteReminder(StateManager.authToken, reminder.id)
                                }
                            }

                            override fun onFailure(p0: Call<Interval>, p1: Throwable) {
                                Toast.makeText(this@NextNewScheduleActivity, getString(R.string.error_while_creating_interval_of_reminder), Toast.LENGTH_SHORT).show()
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
                                    saveHistoricalReminder(reminder, "Frequency", response.body()!!.id)
                                    Toast.makeText(this@NextNewScheduleActivity, getString(R.string.reminder_created_successfully), Toast.LENGTH_SHORT).show()
                                }
                                else {
                                    Toast.makeText(this@NextNewScheduleActivity, getString(R.string.error_while_creating_frequency_of_reminder), Toast.LENGTH_SHORT).show()
                                    medicationApiService.deleteReminder(StateManager.authToken, reminder.id)
                                }
                            }

                            override fun onFailure(p0: Call<Frequency>, p1: Throwable) {
                                Toast.makeText(this@NextNewScheduleActivity, getString(R.string.error_while_creating_frequency_of_reminder), Toast.LENGTH_SHORT).show()
                                medicationApiService.deleteReminder(StateManager.authToken, reminder.id)
                            }

                        })
                    }
                    finish()
                }
                else Toast.makeText(this@NextNewScheduleActivity,
                    getString(R.string.error_while_creating_reminder), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(p0: Call<Reminder>, p1: Throwable) {
                Toast.makeText(this@NextNewScheduleActivity, getString(R.string.error_while_creating_reminder), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun saveHistoricalReminder(reminder: Reminder, type: String, typeId: Long) {
        val createdDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.createdDateString)
        val createdDateParsed = SharedMethods.getDDMMYYStringFromDate(createdDate)
        val endDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.endDateString!!)
        val endDateParsed = SharedMethods.getDDMMYYStringFromDate(endDate)
        AppDatabase.getInstance(this).getHistoricalReminderDao().insertReminder(HistoricalReminder(
            0,
            reminder.createdDateString,
            createdDateParsed,
            reminder.pills,
            reminder.endDateString!!,
            endDateParsed,
            StateManager.selectedMedicine!!.name,
            type,
            typeId,
            reminder.consumeFood,
            reminder.id
        ))
    }

    private fun generateLocalReminderId(): Short {
        val dao = AppDatabase.getInstance(this).getHistoricalReminderDao()
        val existingIds = dao.getAll().map { it.localId }.toSet()

        val random = Random(System.nanoTime())
        while (true) {
            val localId = (random.nextInt(Short.MAX_VALUE.toInt() - 200 + 1) + 200).toShort()
            if (localId !in existingIds) {
                return localId
            }
        }
    }

    private fun saveHistoricalReminder(reminder: Reminder, type: String, typeId: Long, customMedicine: String) {
        val createdDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.createdDateString)
        val createdDateParsed = SharedMethods.getDDMMYYStringFromDate(createdDate)
        val endDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.endDateString!!)
        val endDateParsed = SharedMethods.getDDMMYYStringFromDate(endDate)
        localId = generateLocalReminderId()
        Log.d("Creating with local id", localId.toString())
        AppDatabase.getInstance(this).getHistoricalReminderDao().insertReminder(HistoricalReminder(
            0,
            reminder.createdDateString,
            createdDateParsed,
            reminder.pills,
            reminder.endDateString!!,
            endDateParsed,
            customMedicine,
            type,
            typeId,
            reminder.consumeFood,
            reminder.id,
            localId
        ))
    }

    private fun createSchedule() {
        val now = LocalDateTime.now()

        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText(getString(R.string.select_reminder_start_time))
            .build()

        val rgrpFood = findViewById<RadioGroup>(R.id.rgrpFood)


        val selectedFoodRadio = findViewById<RadioButton>(rgrpFood.checkedRadioButtonId)
        var consumedFood: Boolean?
        try {
            val foodOption = selectedFoodRadio.text.toString()
            consumedFood = when(foodOption) {
                getString(R.string.yes) -> true
                getString(R.string.no) -> false
                getString(R.string.it_doesn_t_matter) -> null
                else -> null
            }
        }
        catch (e: Exception) {
            consumedFood = null
        }

        if ((swInterval.isChecked && (spnIntervalTime.selectedItem.toString() == "6" || spnIntervalTime.selectedItem.toString() == "8") && spnIntervalTimeType.selectedItem.toString() == getString(R.string.hours)) ||
            swFrequency.isChecked && spnFreqTimes.selectedItem.toString() == "2" && spnPer.selectedItem.toString() == getString(R.string.day)) {
            val createdDate = now.plusDays(1)
            val endDateString: String? = when(lapseType) {
                getString(R.string.days) -> SharedMethods.getJSDateFromLocalDateTime(createdDate.plusDays(lapseTime.toLong() - 1L))
                getString(R.string.weeks) -> SharedMethods.getJSDateFromLocalDateTime(createdDate.plusWeeks(lapseTime.toLong()))
                else -> null
            }
            if (StateManager.selectedMedicine!!.name == getString(R.string.other))
                saveReminderWithCustomMedicine(createdDate, endDateString, consumedFood)
            else
                makeHttpRequest(createdDate, endDateString, consumedFood)
        }
        else if ((swInterval.isChecked && spnIntervalTime.selectedItem.toString() == "12" && spnIntervalTimeType.selectedItem.toString() == getString(R.string.hours)) ||
            (swInterval.isChecked && spnIntervalTimeType.selectedItem.toString() == getString(R.string.days)) ||
            (swFrequency.isChecked && spnFreqTimes.selectedItem.toString() == "1" && spnPer.selectedItem.toString() == getString(R.string.day))) {


            timePicker.show(supportFragmentManager, "Reminder time")
            timePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this@NextNewScheduleActivity, getString(R.string.you_need_to_indicate_the_reminder_start_time), Toast.LENGTH_SHORT).show()
            }
            timePicker.addOnPositiveButtonClickListener {
                val nowHour = LocalTime.of(now.hour, now.minute)
                val createHour = LocalTime.of(timePicker.hour, timePicker.minute)
                var createdDate = LocalDateTime.of(now.toLocalDate(), createHour)
                if (nowHour >= createHour) {
                    createdDate = createdDate.plusDays(1)
                }
                val endDateString: String? = when(lapseType) {
                    getString(R.string.days) -> SharedMethods.getJSDateFromLocalDateTime(createdDate.plusDays(lapseTime.toLong() - 1L))
                    getString(R.string.weeks) -> SharedMethods.getJSDateFromLocalDateTime(createdDate.plusWeeks(lapseTime.toLong()))
                    else -> null
                }
                if (StateManager.selectedMedicine!!.name == getString(R.string.other))
                    saveReminderWithCustomMedicine(createdDate, endDateString, consumedFood)
                else
                    makeHttpRequest(createdDate, endDateString, consumedFood)
            }
        }

    }
}
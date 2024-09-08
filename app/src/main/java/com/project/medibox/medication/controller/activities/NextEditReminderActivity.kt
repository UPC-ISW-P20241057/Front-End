package com.project.medibox.medication.controller.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import com.project.medibox.medication.resources.UpdateFrequencyResource
import com.project.medibox.medication.resources.UpdateIntervalResource
import com.project.medibox.medication.resources.UpdateReminderResource
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

class NextEditReminderActivity : AppCompatActivity() {

    private lateinit var spnIntervalTime: Spinner
    private lateinit var spnIntervalTimeType: Spinner

    private lateinit var spnFreqTimes: Spinner
    private lateinit var spnPer: Spinner

    private lateinit var spnForTime: Spinner
    private lateinit var spnForTimeType: Spinner

    private lateinit var cvEditInterval: CardView
    private lateinit var cvEditFrequency: CardView
    private lateinit var updateDate: LocalDateTime

    private var interval = CreateIntervalResource("", -1, -1)
    private var frequency = CreateFrequencyResource("", -1, -1)

    private var lapseTime: Int = -1
    private var lapseType: String = ""

    private lateinit var timePicker: MaterialTimePicker
    private var reminderType: String = ""

    private lateinit var imageDao: MedicineImageDAO
    private var bitmap: Bitmap? = null
    private var existingImage: MedicineImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_next_edit_reminder)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        reminderType = StateManager.selectedHistoricalReminder.type
        when(reminderType) {
            "Interval" -> showIntervalCard()
            "Frequency" -> showFrequencyCard()
        }

        imageDao = AppDatabase.getInstance(this).getMedicineImageDao()

        val btnEditReminder = findViewById<Button>(R.id.btnEditReminder)
        btnEditReminder.setOnClickListener {
            editReminder()
        }
        spnIntervalTime = findViewById(R.id.spnIntervalTime)
        spnIntervalTimeType = findViewById(R.id.spnIntervalTimeType)

        spnFreqTimes = findViewById(R.id.spnFrecTimes)
        spnPer = findViewById(R.id.spnPer)

        spnForTime = findViewById(R.id.spnForTime)
        spnForTimeType = findViewById(R.id.spnForTimeType)

        loadSpinners()

        val cvUploadPhoto = findViewById<CardView>(R.id.cvUploadPhoto)
        cvUploadPhoto.setOnClickListener {
            requestImagePermission()
        }

        val cvTakePhoto = findViewById<CardView>(R.id.cvTakePhoto)
        cvTakePhoto.setOnClickListener {
            requestCameraPermission()
        }

        val btnDeletePhoto = findViewById<Button>(R.id.btnDeletePhoto)
        btnDeletePhoto.setOnClickListener {
            deleteMedicinePhoto()
        }
    }

    private fun deleteMedicinePhoto() {
        existingImage = imageDao.getImageByMedicineName(StateManager.selectedMedicine!!.name)
        if (existingImage != null){
            imageDao.deleteImageByMedicineName(existingImage!!.medicineName)
            Toast.makeText(this, "Image deleted successfully!", Toast.LENGTH_SHORT).show()
        }
        else Toast.makeText(this, "Image doesn't exist.", Toast.LENGTH_SHORT).show()
    }

    private val requestGalleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageFromGallery()
        }
        else {
            Toast.makeText(this, "You need permission to select image.", Toast.LENGTH_SHORT).show()
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
        existingImage = imageDao.getImageByMedicineName(StateManager.selectedMedicine!!.name)
        if (existingImage == null)
            imageDao.insertImage(
                MedicineImage(
                0,
                imageString,
                StateManager.selectedMedicine!!.name
            )
            )
        else
            imageDao.updateImage(
                MedicineImage(
                existingImage!!.id,
                imageString,
                StateManager.selectedMedicine!!.name
            )
            )
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
            Toast.makeText(this, getString(R.string.you_need_permission_to_select_image), Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, getString(R.string.cancelled), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startForActivityCamera.launch(intent)
    }

    private fun makeHttpRequest(startDate: LocalDateTime, endDateString: String?, consumedFood: Boolean?) {
        val medicationApiService = SharedMethods.retrofitServiceBuilder(MedicationApiService::class.java)
        val putReminderRequest = medicationApiService.updateReminder(StateManager.authToken, StateManager.selectedHistoricalReminder.reminderId, CreateReminderResource(
            SharedMethods.getJSDateFromLocalDateTime(startDate),
            null,
            endDateString,
            StateManager.selectedMedicine!!.id,
            StateManager.loggedUserId,
            consumedFood
        )
        )

        putReminderRequest.enqueue(object : Callback<Reminder> {
            override fun onResponse(call: Call<Reminder>, reminderResponse: Response<Reminder>) {
                if (reminderResponse.isSuccessful) {
                    val reminder = reminderResponse.body()!!
                    if (reminderType == "Interval") {
                        interval.reminderId = reminder.id
                        val postIntervalRequest = medicationApiService.updateInterval(StateManager.authToken, StateManager.selectedHistoricalReminder.typeId, interval)
                        postIntervalRequest.enqueue(object : Callback<Interval> {
                            override fun onResponse(call: Call<Interval>, response: Response<Interval>) {
                                if (response.isSuccessful) {
                                    updateHistoricalReminder(reminder, "Interval", response.body()!!.id)
                                    reminder.createdDateString = SharedMethods.getJSDateFromLocalDateTime(updateDate)
                                    AppDatabase.getInstance(this@NextEditReminderActivity).getUpcomingReminderAlarmDao().deleteAllByReminderId(reminder.id)
                                    ReminderService.createAlarms(this@NextEditReminderActivity, reminder, response.body()!!)
                                    Toast.makeText(this@NextEditReminderActivity,
                                        getString(R.string.reminder_updated_successfully), Toast.LENGTH_SHORT).show()
                                }

                                else {
                                    Toast.makeText(this@NextEditReminderActivity,
                                        getString(R.string.error_while_creating_interval_of_reminder), Toast.LENGTH_SHORT).show()
                                    val r = medicationApiService.deleteReminder(StateManager.authToken, reminder.id)
                                    r.enqueue(object : Callback<Reminder> {
                                        override fun onResponse(
                                            p0: Call<Reminder>,
                                            p1: Response<Reminder>
                                        ) {

                                        }

                                        override fun onFailure(p0: Call<Reminder>, p1: Throwable) {

                                        }

                                    })
                                    AppDatabase.getInstance(this@NextEditReminderActivity).getHistoricalReminderDao().deleteById(StateManager.selectedHistoricalReminder.id)
                                    AppDatabase.getInstance(this@NextEditReminderActivity).getUpcomingReminderAlarmDao().deleteAllByReminderId(reminder.id)
                                }
                            }

                            override fun onFailure(p0: Call<Interval>, p1: Throwable) {
                                Toast.makeText(this@NextEditReminderActivity, getString(R.string.error_while_creating_interval_of_reminder), Toast.LENGTH_SHORT).show()
                                val r = medicationApiService.deleteReminder(StateManager.authToken, reminder.id)
                                r.enqueue(object : Callback<Reminder> {
                                    override fun onResponse(
                                        p0: Call<Reminder>,
                                        p1: Response<Reminder>
                                    ) {

                                    }

                                    override fun onFailure(p0: Call<Reminder>, p1: Throwable) {

                                    }

                                })
                                AppDatabase.getInstance(this@NextEditReminderActivity).getHistoricalReminderDao().deleteById(StateManager.selectedHistoricalReminder.id)
                                AppDatabase.getInstance(this@NextEditReminderActivity).getUpcomingReminderAlarmDao().deleteAllByReminderId(reminder.id)
                            }

                        })
                    }
                    else if (reminderType == "Frequency") {
                        frequency.reminderId = reminder.id
                        val postFrequencyRequest = medicationApiService.updateFrequency(StateManager.authToken, StateManager.selectedHistoricalReminder.typeId, frequency)
                        postFrequencyRequest.enqueue(object : Callback<Frequency> {
                            override fun onResponse(call: Call<Frequency>, response: Response<Frequency>) {
                                if (response.isSuccessful) {
                                    updateHistoricalReminder(reminder, "Frequency", response.body()!!.id)
                                    AppDatabase.getInstance(this@NextEditReminderActivity).getUpcomingReminderAlarmDao().deleteAllByReminderId(reminder.id)
                                    ReminderService.createAlarms(this@NextEditReminderActivity, reminder, response.body()!!)
                                    Toast.makeText(this@NextEditReminderActivity, getString(R.string.reminder_updated_successfully), Toast.LENGTH_SHORT).show()
                                }
                                else {
                                    Toast.makeText(this@NextEditReminderActivity, getString(R.string.error_while_creating_frequency_of_reminder), Toast.LENGTH_SHORT).show()
                                    val r = medicationApiService.deleteReminder(StateManager.authToken, reminder.id)
                                    r.enqueue(object : Callback<Reminder> {
                                        override fun onResponse(
                                            p0: Call<Reminder>,
                                            p1: Response<Reminder>
                                        ) {

                                        }

                                        override fun onFailure(p0: Call<Reminder>, p1: Throwable) {

                                        }

                                    })
                                    AppDatabase.getInstance(this@NextEditReminderActivity).getHistoricalReminderDao().deleteById(StateManager.selectedHistoricalReminder.id)
                                    AppDatabase.getInstance(this@NextEditReminderActivity).getUpcomingReminderAlarmDao().deleteAllByReminderId(reminder.id)
                                }
                            }

                            override fun onFailure(p0: Call<Frequency>, p1: Throwable) {
                                Toast.makeText(this@NextEditReminderActivity, getString(R.string.error_while_creating_frequency_of_reminder), Toast.LENGTH_SHORT).show()
                                val r = medicationApiService.deleteReminder(StateManager.authToken, reminder.id)
                                r.enqueue(object : Callback<Reminder> {
                                    override fun onResponse(
                                        p0: Call<Reminder>,
                                        p1: Response<Reminder>
                                    ) {
                                    }

                                    override fun onFailure(p0: Call<Reminder>, p1: Throwable) {

                                    }

                                })
                                AppDatabase.getInstance(this@NextEditReminderActivity).getHistoricalReminderDao().deleteById(StateManager.selectedHistoricalReminder.id)
                                AppDatabase.getInstance(this@NextEditReminderActivity).getUpcomingReminderAlarmDao().deleteAllByReminderId(reminder.id)
                            }

                        })
                    }
                    finish()
                }
                else Toast.makeText(this@NextEditReminderActivity,
                    getString(R.string.error_while_updating_reminder), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(p0: Call<Reminder>, p1: Throwable) {
                Toast.makeText(this@NextEditReminderActivity, getString(R.string.error_while_updating_reminder), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateHistoricalReminder(reminder: Reminder, type: String, typeId: Long) {
        val createdDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.createdDateString)
        val createdDateParsed = SharedMethods.getDDMMYYStringFromDate(createdDate)
        val endDate = SharedMethods.getLocalDateTimeFromJSDate(reminder.endDateString!!)
        val endDateParsed = SharedMethods.getDDMMYYStringFromDate(endDate)
        AppDatabase.getInstance(this).getHistoricalReminderDao().updateReminder(
            HistoricalReminder(
            StateManager.selectedHistoricalReminder.id,
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
        )
        )
    }

    private fun editReminder() {
        val now = LocalDateTime.now()
        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText(getString(R.string.select_reminder_start_time))
            .build()

        val rgrpFood = findViewById<RadioGroup>(R.id.rgrpFood)


        val selectedFoodRadio = findViewById<RadioButton>(rgrpFood.checkedRadioButtonId)
        val foodOption = selectedFoodRadio.text.toString()
        val consumedFood: Boolean? = when(foodOption) {
            getString(R.string.yes) -> true
            getString(R.string.no) -> false
            getString(R.string.it_doesn_t_matter) -> null
            else -> null
        }


        if ((reminderType == "Interval" && (spnIntervalTime.selectedItem.toString() == "6" || spnIntervalTime.selectedItem.toString() == "8") && spnIntervalTimeType.selectedItem.toString() == getString(
                R.string.hours
            )) ||
            reminderType == "Frequency" && spnFreqTimes.selectedItem.toString() == "2" && spnPer.selectedItem.toString() == getString(R.string.day)) {
            updateDate = now.plusDays(1)
            val createdDate = SharedMethods.getLocalDateTimeFromJSDate(StateManager.selectedHistoricalReminder.createdDate)
            val endDateString: String? = when(lapseType) {
                getString(R.string.days) -> SharedMethods.getJSDateFromLocalDateTime(createdDate.plusDays(lapseTime.toLong()))
                getString(R.string.weeks) -> SharedMethods.getJSDateFromLocalDateTime(createdDate.plusWeeks(lapseTime.toLong()))
                else -> null
            }
            makeHttpRequest(createdDate, endDateString, consumedFood)
        }
        else if ((reminderType == "Interval" && spnIntervalTime.selectedItem.toString() == "12" && spnIntervalTimeType.selectedItem.toString() == getString(R.string.hours)) ||
            (reminderType == "Interval" && spnIntervalTimeType.selectedItem.toString() == getString(R.string.days)) ||
            (reminderType == "Frequency" && spnFreqTimes.selectedItem.toString() == "1" && spnPer.selectedItem.toString() == getString(R.string.day))) {


            timePicker.show(supportFragmentManager, "Reminder time")
            timePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this@NextEditReminderActivity,
                    getString(R.string.you_need_to_indicate_the_reminder_start_time), Toast.LENGTH_SHORT).show()
            }
            timePicker.addOnPositiveButtonClickListener {
                val nowHour = LocalTime.of(now.hour, now.minute)
                val createHour = LocalTime.of(timePicker.hour, timePicker.minute)
                var createdDate = SharedMethods.getLocalDateTimeFromJSDate(StateManager.selectedHistoricalReminder.createdDate)
                if (nowHour >= createHour) {
                    createdDate = createdDate.plusDays(1)
                }
                val endDateString: String? = when(lapseType) {
                    getString(R.string.days) -> SharedMethods.getJSDateFromLocalDateTime(createdDate.plusDays(lapseTime.toLong()))
                    getString(R.string.weeks) -> SharedMethods.getJSDateFromLocalDateTime(createdDate.plusWeeks(lapseTime.toLong()))
                    else -> null
                }
                makeHttpRequest(createdDate, endDateString, consumedFood)
            }
        }
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

    private fun showFrequencyCard() {
        cvEditFrequency = findViewById(R.id.cvEditFrequency)
        cvEditFrequency.visibility = View.VISIBLE
    }

    private fun showIntervalCard() {
        cvEditInterval = findViewById(R.id.cvEditInterval)
        cvEditInterval.visibility = View.VISIBLE
    }
}
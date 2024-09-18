package com.project.medibox.medication.controller.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.medication.models.CompletedReminderAlarm
import com.project.medibox.medication.models.MedicineImage
import com.project.medibox.medication.models.MissedReminderAlarm
import com.project.medibox.medication.persistence.UpcomingReminderAlarmDAO
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.BitmapConverter
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import java.time.LocalDateTime
import kotlin.random.Random

class MedicationAlarmWithImageActivity : AppCompatActivity() {
    private var bitmap: Bitmap? = null
    private lateinit var image: MedicineImage
    private lateinit var ivMedicinePic: ImageView

    private fun generateNotificationId(dao: UpcomingReminderAlarmDAO): Int {
        val existingIds = dao.getAll().map { it.notificationId }.toSet()
        val random = Random(System.nanoTime())
        while (true) {
            val notificationId = random.nextInt(Int.MAX_VALUE - 200 + 1) + 200
            if (notificationId !in existingIds) {
                return notificationId
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_medication_alarm_with_image)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val tvImMedicine = findViewById<TextView>(R.id.tvImMedicine)
        val btnMediImAccept = findViewById<Button>(R.id.btnMediImAccept)
        val btnMediImMissed = findViewById<Button>(R.id.btnMediImMissed)
        val btnMediInPostpone = findViewById<Button>(R.id.btnMediImPostpone)
        val tvImAlarmWithFood = findViewById<TextView>(R.id.tvImAlarmWithFood)
        ivMedicinePic = findViewById(R.id.ivMedicinePic)
        val upcomingAlarm = StateManager.selectedUpcomingAlarm

        tvImMedicine.text = upcomingAlarm.medicineName

        tvImAlarmWithFood.text = when (upcomingAlarm.consumeFood) {
            true -> getString(R.string.with_food)
            false -> getString(R.string.without_food)
            null -> ""
        }


        btnMediImAccept.setOnClickListener {
            AppDatabase.getInstance(this).getUpcomingReminderAlarmDao().deleteById(upcomingAlarm.id)
            AppDatabase.getInstance(this).getCompletedReminderAlarmDao().insertAlarm(
                CompletedReminderAlarm(
                    0,
                    upcomingAlarm.medicineName,
                    upcomingAlarm.activateDateString,
                    upcomingAlarm.activateHour,
                    upcomingAlarm.activateMinute,
                    upcomingAlarm.consumeFood
                )
            )
            finish()
        }
        btnMediImMissed.setOnClickListener {
            AppDatabase.getInstance(this).getUpcomingReminderAlarmDao().deleteById(upcomingAlarm.id)
            AppDatabase.getInstance(this).getMissedReminderAlarmDao().insertAlarm(
                MissedReminderAlarm(
                    0,
                    upcomingAlarm.medicineName,
                    upcomingAlarm.activateDateString,
                    upcomingAlarm.activateHour,
                    upcomingAlarm.activateMinute,
                    upcomingAlarm.consumeFood
                )
            )
            finish()
        }
        btnMediInPostpone.setOnClickListener {
            val dao = AppDatabase.getInstance(this).getUpcomingReminderAlarmDao()
            var localDateTime = LocalDateTime.now()
            localDateTime = localDateTime.plusMinutes(10)
            upcomingAlarm.activateHour = localDateTime.hour
            upcomingAlarm.activateMinute = localDateTime.minute
            upcomingAlarm.activateDateString = SharedMethods.getDDMMYYStringFromDate(localDateTime)
            upcomingAlarm.notified = false
            upcomingAlarm.notificationId = generateNotificationId(dao)
            dao.updateAlarm(upcomingAlarm)
            finish()
        }
        ivMedicinePic.setOnClickListener {
            requestImagePermission()
        }
    }

    override fun onResume() {
        super.onResume()
        setMedicineImage()
    }

    private fun setMedicineImage() {
        image = StateManager.selectedMedicineImage
        val bitmapFromString = BitmapConverter.converterStringToBitmap(image.imageString)
        ivMedicinePic.setImageBitmap(bitmapFromString)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImageFromGallery()
        }
        else {
            Toast.makeText(this,
                getString(R.string.you_need_permission_to_select_image), Toast.LENGTH_SHORT).show()
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
                requestPermissionLauncher.launch(permission)
            }
        }
    }


    private fun saveMedicineImage() {
        val imageDao = AppDatabase.getInstance(this).getMedicineImageDao()
        val imageString = BitmapConverter.converterBitmapToString(bitmap!!)
        val medicineImage = MedicineImage(image.id, imageString, image.medicineName)
        imageDao.updateImage(medicineImage)
        StateManager.selectedMedicineImage = medicineImage
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
}
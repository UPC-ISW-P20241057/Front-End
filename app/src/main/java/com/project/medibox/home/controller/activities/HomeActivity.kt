package com.project.medibox.home.controller.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.medibox.R
import com.project.medibox.home.controller.fragments.CalendarFragment
import com.project.medibox.home.controller.fragments.DashboardFragment
import com.project.medibox.home.controller.fragments.HomeFragment
import com.project.medibox.home.controller.fragments.ProfileFragment
import com.project.medibox.identitymanagement.controller.activities.LoginActivity
import com.project.medibox.identitymanagement.services.PermanentLoginService
import com.project.medibox.medication.services.ReminderService
import com.project.medibox.pillboxmanagement.services.EmptyPillboxService
import com.project.medibox.shared.AppDatabase
import com.project.medibox.home.controller.fragments.VoiceCommandsFragment

class HomeActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        navigateTo(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.RECORD_AUDIO),
                0
            )
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                0
            )
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bnvMenu)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Cargar el fragmento predeterminado al iniciar la actividad
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.flFragment, HomeFragment())
                .commit()
        }

        startServices()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            Toast.makeText(this, "You need to provide microphone permission to invoke voice commands.", Toast.LENGTH_SHORT).show()
    }

    private fun startServices() {
        EmptyPillboxService.startService(this)
        PermanentLoginService.startService(this)
        ReminderService.startService(this)
    }

    private fun stopServices() {
        EmptyPillboxService.stopService(this)
        PermanentLoginService.stopService(this)
        ReminderService.stopService(this)
    }

    private fun navigateTo(item: MenuItem): Boolean {
        item.isChecked = true
        return supportFragmentManager
            .beginTransaction()
            .replace(R.id.flFragment, getFragmentFor(item))
            .commit() > 0
    }

    private fun getFragmentFor(item: MenuItem): Fragment {
        return when (item.itemId) {
            R.id.menu_home -> HomeFragment()
            R.id.menu_dashboard -> DashboardFragment()
            R.id.menu_voice -> VoiceCommandsFragment()
            R.id.menu_calendar -> CalendarFragment()
            R.id.menu_profile -> ProfileFragment()
            else -> HomeFragment()
        }
    }

    fun signOut() {
        stopServices()
        AppDatabase.getInstance(this).getLoginCredentialsDao().cleanTable()
        val intent = Intent(this, LoginActivity::class.java) // Cambia a LoginActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}

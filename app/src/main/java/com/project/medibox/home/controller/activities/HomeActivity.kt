package com.project.medibox.home.controller.activities

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
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
import com.project.medibox.identitymanagement.services.PermanentLoginService
import com.project.medibox.medication.services.ReminderService
import com.project.medibox.pillboxmanagement.services.EmptyPillboxService
import com.project.medibox.shared.AppDatabase

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
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
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
            R.id.menu_calendar -> CalendarFragment()
            R.id.menu_profile -> ProfileFragment()
            else -> HomeFragment()
        }
    }

    fun signOut() {
        stopServices()
        AppDatabase.getInstance(this).getLoginCredentialsDao().cleanTable()
        finish()
    }
}

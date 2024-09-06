package com.project.medibox.home.controller.activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
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
import com.project.medibox.pillboxmanagement.models.Pillbox
import com.project.medibox.pillboxmanagement.network.PillboxApiService
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.RECORD_AUDIO),
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

        if (StateManager.selectedPillboxId < 1)
            openSavePillboxDialog()

        startServices()
    }

    private fun openSavePillboxDialog() {
        val changePillboxIdDialog = Dialog(this)
        changePillboxIdDialog.setContentView(R.layout.dialog_change_pillbox_id)
        changePillboxIdDialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        changePillboxIdDialog.window!!.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.bg_dialog_generic_options))
        changePillboxIdDialog.setCancelable(false)
        val etSelectPillboxId = changePillboxIdDialog.findViewById<EditText>(R.id.etSelectPillboxId)
        val btnAcceptPillboxId = changePillboxIdDialog.findViewById<Button>(R.id.btnAcceptPillboxId)

        btnAcceptPillboxId.setOnClickListener {
            if (SharedMethods.isStringAnULong(etSelectPillboxId.text.toString())) {
                val selectedId = etSelectPillboxId.text.toString().toLong()
                val pillboxApiService = SharedMethods.retrofitServiceBuilder(PillboxApiService::class.java)
                val request = pillboxApiService.getPillboxData(selectedId)
                request.enqueue(object : Callback<Pillbox> {
                    override fun onResponse(call: Call<Pillbox>, response: Response<Pillbox>) {
                        if (response.isSuccessful) {
                            StateManager.selectedPillboxId = selectedId
                            Toast.makeText(this@HomeActivity,
                                getString(R.string.pillbox_id_saved_correctly), Toast.LENGTH_SHORT).show()
                            changePillboxIdDialog.dismiss()
                        }
                        else
                            Toast.makeText(this@HomeActivity,
                                getString(R.string.invalid_id), Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<Pillbox>, t: Throwable) {
                        Toast.makeText(this@HomeActivity,
                            getString(R.string.error_occurred_in_pillbox_server), Toast.LENGTH_SHORT).show()
                    }

                })

            }
            else
                Toast.makeText(this,
                    getString(R.string.introduce_valid_natural_number), Toast.LENGTH_SHORT).show()
        }

        changePillboxIdDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
            Toast.makeText(this, getString(R.string.need_microphone_permission), Toast.LENGTH_SHORT).show()
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
        AppDatabase.getInstance(this).getToneSettingsDao().cleanSettings()
        val intent = Intent(this, LoginActivity::class.java) // Cambia a LoginActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}

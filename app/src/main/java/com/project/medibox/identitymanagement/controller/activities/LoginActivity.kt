package com.project.medibox.identitymanagement.controller.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.home.controller.activities.HomeActivity
import com.project.medibox.identitymanagement.models.AuthenticationRequest
import com.project.medibox.identitymanagement.models.AuthenticationResponse
import com.project.medibox.identitymanagement.models.LoginCredentials
import com.project.medibox.identitymanagement.models.User
import com.project.medibox.identitymanagement.network.UserApiService
import com.project.medibox.medication.models.ApiAlarm
import com.project.medibox.medication.network.MedicationApiService
import com.project.medibox.medication.services.ReminderService
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun goToSignUpActivity(view: View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
    fun signIn(view: View){
        val etLoginEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etLoginPassword = findViewById<EditText>(R.id.etLoginPassword)
        val userApiService: UserApiService = SharedMethods.retrofitServiceBuilder(UserApiService::class.java)

        val request = userApiService.signIn(AuthenticationRequest(
            etLoginEmail.text.toString(),
            etLoginPassword.text.toString()
        ))

        request.enqueue(object : Callback<AuthenticationResponse>{
            override fun onResponse(
                call: Call<AuthenticationResponse>,
                response: Response<AuthenticationResponse>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.role == "User") {
                        AppDatabase.getInstance(this@LoginActivity).getLoginCredentialsDao().cleanTable()
                        AppDatabase.getInstance(this@LoginActivity).getLoginCredentialsDao().insertCredentials(
                            LoginCredentials(null, etLoginEmail.text.toString(), etLoginPassword.text.toString())
                        )
                        goToHome(response.body()!!)
                    }
                    else Toast.makeText(this@LoginActivity,
                        getString(R.string.admin_account_denied), Toast.LENGTH_SHORT).show()
                }
                else
                    Toast.makeText(this@LoginActivity,
                        getString(R.string.error_while_logging_in), Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<AuthenticationResponse>, p1: Throwable) {
                Toast.makeText(this@LoginActivity, call.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun goToHome(authenticateResponse: AuthenticationResponse) {
        StateManager.authToken = "Bearer ${authenticateResponse.jwtToken}"
        StateManager.loggedUser = User(
            authenticateResponse.id,
            authenticateResponse.email,
            authenticateResponse.role,
            authenticateResponse.phone,
            authenticateResponse.name,
            authenticateResponse.lastName,
        )
        StateManager.loggedUserId = authenticateResponse.id
        ReminderService.loadAlarmsFromApi(this)
        AppDatabase.getInstance(this).getToneSettingsDao().createSettings()
        getCompletedAndMissedAlarms()
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun getCompletedAndMissedAlarms() {
        val medicationApiService = SharedMethods.retrofitServiceBuilder(MedicationApiService::class.java)
        val completedRequest = medicationApiService.getCompletedAlarmsByUserId(StateManager.authToken, StateManager.loggedUserId)
        completedRequest.enqueue(object : Callback<List<ApiAlarm>> {
            override fun onResponse(call: Call<List<ApiAlarm>>, response: Response<List<ApiAlarm>>) {
                if (response.isSuccessful) {
                    val completedAlarms = SharedMethods.mapApiAlarmListToCompletedAlarmList(response.body()!!)
                    completedAlarms.forEach {
                        AppDatabase.getInstance(this@LoginActivity).getCompletedReminderAlarmDao().insertAlarm(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<ApiAlarm>>, t: Throwable) {
                Toast.makeText(this@LoginActivity,
                    getString(R.string.error_while_getting_completed_alarms), Toast.LENGTH_SHORT).show()
            }

        })

        val missedRequest = medicationApiService.getMissedAlarmsByUserId(StateManager.authToken, StateManager.loggedUserId)
        missedRequest.enqueue(object : Callback<List<ApiAlarm>> {
            override fun onResponse(call: Call<List<ApiAlarm>>, response: Response<List<ApiAlarm>>) {
                if (response.isSuccessful) {
                    val missedAlarms = SharedMethods.mapApiAlarmListToMissedAlarmList(response.body()!!)
                    missedAlarms.forEach {
                        AppDatabase.getInstance(this@LoginActivity).getMissedReminderAlarmDao().insertAlarm(it)
                    }
                }
            }

            override fun onFailure(call: Call<List<ApiAlarm>>, t: Throwable) {
                Toast.makeText(this@LoginActivity,
                    getString(R.string.error_while_getting_missed_alarms), Toast.LENGTH_SHORT).show()
            }

        })
    }
}
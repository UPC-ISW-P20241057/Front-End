package com.project.medibox.controllers.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.project.medibox.R
import com.project.medibox.identitymanagement.controller.activities.LoginActivity
import com.project.medibox.identitymanagement.models.AuthenticationRequest
import com.project.medibox.identitymanagement.models.AuthenticationResponse
import com.project.medibox.identitymanagement.models.User
import com.project.medibox.identitymanagement.network.UserService
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        autoLogin()
    }

    private fun autoLogin() {
        val query = AppDatabase.getInstance(this).getLoginCredentialsDao().getAll()
        if (query.isNotEmpty()) {
            val loginCredentials = query[0]
            val userService = SharedMethods.retrofitServiceBuilder(UserService::class.java)

            val request = userService.signIn(AuthenticationRequest(loginCredentials.email, loginCredentials.password))

            request.enqueue(object : Callback<AuthenticationResponse> {
                override fun onResponse(
                    call: Call<AuthenticationResponse>,
                    response: Response<AuthenticationResponse>
                ) {
                    if (response.isSuccessful)
                    {
                        goToHome(response.body()!!)
                    }
                    else {
                        AppDatabase.getInstance(this@MainActivity).getLoginCredentialsDao().cleanTable()
                        Toast.makeText(this@MainActivity, "Error al iniciar sesión de forma automatica", Toast.LENGTH_SHORT).show()
                        startLoginActivity()

                    }
                }

                override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                    AppDatabase.getInstance(this@MainActivity).getLoginCredentialsDao().cleanTable()
                    Toast.makeText(this@MainActivity, "Error al iniciar sesión de forma automatica", Toast.LENGTH_SHORT).show()
                    startLoginActivity()
                }

            })
        }
        else startLoginActivity()
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
        StateManager.loggedUserId = authenticateResponse.id
        val intent = Intent(this, DashboardActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}

package com.project.medibox.identitymanagement.controller.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.identitymanagement.models.AuthenticationRequest
import com.project.medibox.identitymanagement.models.AuthenticationResponse
import com.project.medibox.identitymanagement.models.LoginCredentials
import com.project.medibox.identitymanagement.models.User
import com.project.medibox.identitymanagement.network.UserService
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
    fun signIn(view: View){
        val etLoginEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etLoginPassword = findViewById<EditText>(R.id.etLoginPassword)
        val userService: UserService = SharedMethods.retrofitServiceBuilder(UserService::class.java)

        val request = userService.signIn(AuthenticationRequest(
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
                    else Toast.makeText(this@LoginActivity, "Hey, esta es una cuenta administrativa. No puedes iniciar sesión con esta cuenta", Toast.LENGTH_SHORT).show()
                }
                else
                    Toast.makeText(this@LoginActivity, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<AuthenticationResponse>, p1: Throwable) {
                Toast.makeText(this@LoginActivity, call.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun goToHome(authenticateResponse: AuthenticationResponse) {
        Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
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
    }
}
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
import com.project.medibox.identitymanagement.models.RegisterRequest
import com.project.medibox.identitymanagement.models.RegisterResponse
import com.project.medibox.identitymanagement.network.UserApiService
import com.project.medibox.shared.SharedMethods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun signUp(view: View) {
        val etRegName = findViewById<EditText>(R.id.etRegName)
        val etRegLastName = findViewById<EditText>(R.id.etRegLastName)
        val etRegEmail = findViewById<EditText>(R.id.etRegEmail)
        val etRegPhone = findViewById<EditText>(R.id.etRegPhone)
        val etRegPassword = findViewById<EditText>(R.id.etRegPassword)
        val etRepeatRegPassword = findViewById<EditText>(R.id.etRepeatRegPassword)
        val userApiService = SharedMethods.retrofitServiceBuilder(UserApiService::class.java)

        if (etRegPassword.text.toString() == etRepeatRegPassword.text.toString()) {
            val request = userApiService.signUp(RegisterRequest(
                etRegEmail.text.toString(),
                etRegPassword.text.toString(),
                "User",
                etRegPhone.text.toString(),
                etRegName.text.toString(),
                etRegLastName.text.toString()
            ))
            request.enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if(response.isSuccessful)
                        goToRegistrationSuccessfullyActivity()
                    else Toast.makeText(this@RegistrationActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@RegistrationActivity, "An error occurred while registration", Toast.LENGTH_SHORT).show()
                }

            })
        }
        else Toast.makeText(this@RegistrationActivity, "Validation errors occurred", Toast.LENGTH_SHORT).show()

    }

    private fun goToRegistrationSuccessfullyActivity() {
        val intent = Intent(this, RegisterSuccessfullyActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
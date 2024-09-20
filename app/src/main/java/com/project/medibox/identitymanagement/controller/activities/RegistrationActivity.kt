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
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
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
        val formattedEmail = etRegEmail.text.toString().lowercase()
        val emailValidation = SharedMethods.isValidEmail(formattedEmail)
        val passwordValidation = etRegPassword.text.toString() == etRepeatRegPassword.text.toString()
        val numberValidation = SharedMethods.isValidNumberString(etRegPhone.text.toString())
        val name = etRegName.text.toString().trim()
        val lastName = etRegLastName.text.toString().trim()
        val nameValidation = SharedMethods.containsOnlyLettersAndSpaces(name + lastName)
        val strings = listOf(etRegEmail.text.toString(), etRegPassword.text.toString(), etRepeatRegPassword.text.toString(), etRegPhone.text.toString(), name, lastName)
        val stringsNotEmptyValidation = strings.all { it.isNotBlank() }
        if (emailValidation && passwordValidation && numberValidation && nameValidation && stringsNotEmptyValidation) {
            val request = userApiService.signUp(RegisterRequest(
                formattedEmail,
                etRegPassword.text.toString(),
                "User",
                etRegPhone.text.toString(),
                name,
                lastName
            ))
            request.enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>
                ) {
                    if(response.isSuccessful)
                        goToRegistrationSuccessfullyActivity()
                    else {
                        val errorBody = response.errorBody()?.string()
                        val message = JSONObject(errorBody!!).getString("message")
                        Toast.makeText(this@RegistrationActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@RegistrationActivity,
                        getString(R.string.an_error_occurred_while_registration), Toast.LENGTH_SHORT).show()
                }

            })
        }
        else
            SharedMethods.registrationValidationToasts(this, emailValidation, stringsNotEmptyValidation, passwordValidation, numberValidation, nameValidation)
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
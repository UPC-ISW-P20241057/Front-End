package com.project.medibox.profile.controller.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.identitymanagement.models.UpdateRequest
import com.project.medibox.identitymanagement.models.User
import com.project.medibox.identitymanagement.network.UserService
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {
    private lateinit var etEditName: EditText
    private lateinit var etEditLastname: EditText
    private lateinit var etEditEmail: EditText
    private lateinit var etEditPassword: EditText
    private lateinit var etEditCellphone: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnSaveEdit = findViewById<Button>(R.id.btnSaveEdit)
        val btnCancelEdit = findViewById<Button>(R.id.btnCancelEdit)
        etEditName = findViewById(R.id.etEditName)
        etEditLastname = findViewById(R.id.etEditLastname)
        etEditEmail = findViewById(R.id.etEditEmail)
        etEditPassword = findViewById(R.id.etEditPassword)
        etEditCellphone = findViewById(R.id.etEditCellphone)

        etEditName.setText(StateManager.loggedUser.name)
        etEditLastname.setText(StateManager.loggedUser.lastName)
        etEditEmail.setText(StateManager.loggedUser.email)
        etEditCellphone.setText(StateManager.loggedUser.phone)

        btnSaveEdit.setOnClickListener {
            saveChanges()
        }
        btnCancelEdit.setOnClickListener {
            cancelEdition()
        }
    }

    private fun cancelEdition() {
        finish()
    }

    private fun saveChanges() {




        val userService = SharedMethods.retrofitServiceBuilder(UserService::class.java)



        val request = userService.updateUser(StateManager.authToken, StateManager.loggedUserId, UpdateRequest(
            etEditEmail.text.toString(),
            etEditPassword.text.toString(),
            "User",
            etEditCellphone.text.toString(),
            etEditName.text.toString(),
            etEditLastname.text.toString(),
        ))

        request.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@EditProfileActivity, "User updated successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else Toast.makeText(this@EditProfileActivity, "Error while updating user.", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(p0: Call<User>, p1: Throwable) {
                Toast.makeText(this@EditProfileActivity, "Error while updating user..", Toast.LENGTH_SHORT).show()
            }

        })
    }
}
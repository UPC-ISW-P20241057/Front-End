package com.project.medibox.profile.controller.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.identitymanagement.network.UserService
import com.project.medibox.shared.SharedMethods

class EditProfileActivity : AppCompatActivity() {
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
        val etEditName = findViewById<EditText>(R.id.etEditName)
        val etEditLastname = findViewById<EditText>(R.id.etEditLastname)
        val etEditEmail = findViewById<EditText>(R.id.etEditEmail)
        val etEditPassword = findViewById<EditText>(R.id.etEditPassword)
        val etEditCellphone = findViewById<EditText>(R.id.etEditCellphone)

        val userService = SharedMethods.retrofitServiceBuilder(UserService::class.java)


    }
}
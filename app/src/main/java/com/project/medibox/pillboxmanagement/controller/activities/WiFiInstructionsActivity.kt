package com.project.medibox.pillboxmanagement.controller.activities

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.medibox.R
import com.project.medibox.pillboxmanagement.models.Pillbox
import com.project.medibox.pillboxmanagement.network.PillboxApiService
import com.project.medibox.shared.SharedMethods
import com.project.medibox.shared.StateManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WiFiInstructionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_wifi_instructions)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnInstructionsAccept = findViewById<Button>(R.id.btnInstructionsAccept)
        val btnChangePillboxId = findViewById<Button>(R.id.btnChangePillboxId)

        btnInstructionsAccept.setOnClickListener {
            finish()
        }

        btnChangePillboxId.setOnClickListener {
            openChangePillboxDialog()
        }
    }

    private fun openChangePillboxDialog() {
        val changePillboxIdDialog = Dialog(this)
        changePillboxIdDialog.setContentView(R.layout.dialog_change_pillbox_id)
        changePillboxIdDialog.window!!.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        changePillboxIdDialog.window!!.setBackgroundDrawable(AppCompatResources.getDrawable(this, R.drawable.bg_dialog_generic_options))
        changePillboxIdDialog.setCancelable(true)
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
                            Toast.makeText(this@WiFiInstructionsActivity,
                                getString(R.string.pillbox_id_changed_successfully), Toast.LENGTH_SHORT).show()
                            changePillboxIdDialog.dismiss()
                        }
                        else
                            Toast.makeText(this@WiFiInstructionsActivity, "ID inválido", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<Pillbox>, t: Throwable) {
                        Toast.makeText(this@WiFiInstructionsActivity, "Ha ocurrido un error en el servidor de pastilleros.", Toast.LENGTH_SHORT).show()
                    }

                })

            }
            else
                Toast.makeText(this,
                    getString(R.string.introduce_valid_natural_number), Toast.LENGTH_SHORT).show()
        }

        changePillboxIdDialog.show()
    }
}
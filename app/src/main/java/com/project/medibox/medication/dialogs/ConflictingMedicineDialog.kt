package com.project.medibox.medication.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.project.medibox.medication.controller.activities.NewScheduleActivity

class ConflictingMedicineDialog(private val name1: String, private val name2: String): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val parentActivity = activity as NewScheduleActivity
        return parentActivity.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Medicine $name1 has conflicting with your existing medicine $name2. Do you want to continue creating the reminder?")
                .setPositiveButton("No") { _, _ ->
                    it.finish()
                }
                .setNegativeButton("Continue") { _, _ ->
                    it.goToNextActivity()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
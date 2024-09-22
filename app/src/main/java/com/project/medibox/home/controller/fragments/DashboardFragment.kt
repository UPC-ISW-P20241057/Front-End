package com.project.medibox.home.controller.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.project.medibox.R
import com.project.medibox.medication.controller.activities.MedicationHistoryActivity
import com.project.medibox.medication.controller.activities.MedicationProgressActivity
import com.project.medibox.medication.controller.activities.NewScheduleActivity
import com.project.medibox.pillboxmanagement.controller.activities.CustomizeAlarmActivity
import com.project.medibox.pillboxmanagement.controller.activities.WiFiInstructionsActivity
import com.project.medibox.shared.SharedMethods

class DashboardFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_dashboard, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val cvScheduleReminder = view.findViewById<CardView>(R.id.cvScheduleReminder)
    val ivWifiInstructions = view.findViewById<ImageView>(R.id.ivWifiInstructions)
    val cvCustomizeAlarm = view.findViewById<CardView>(R.id.cvCustomizeAlarm)
    val cvDboardMedHistory = view.findViewById<CardView>(R.id.cvDboardMedHistory)
    val cvMedProcess = view.findViewById<CardView>(R.id.cvMedProcess)
    cvScheduleReminder.setOnClickListener {
      goToNewScheduleActivity()
    }
    ivWifiInstructions.setOnClickListener {
      goToWifiInstructionsActivity()
    }
    cvDboardMedHistory.setOnClickListener {
      goToMedicationHistoryActivity()
    }
    cvCustomizeAlarm.setOnClickListener {
      goToCustomizeAlarmActivity()
    }
    cvMedProcess.setOnClickListener {
      goToMedicationProcessActivity()
    }
    if (SharedMethods.isDarkTheme(requireActivity())) {
      ivWifiInstructions.setImageResource(R.mipmap.wifi_instructions_white)
    }
  }

  private fun goToMedicationProcessActivity() {
    val intent = Intent(requireContext(), MedicationProgressActivity::class.java)
    startActivity(intent)
  }

  private fun goToMedicationHistoryActivity() {
    val intent = Intent(requireContext(), MedicationHistoryActivity::class.java)
    startActivity(intent)
  }

  private fun goToWifiInstructionsActivity() {
    val intent = Intent(requireContext(), WiFiInstructionsActivity::class.java)
    startActivity(intent)
  }

  private fun goToNewScheduleActivity() {
    val intent = Intent(requireContext(), NewScheduleActivity::class.java)
    startActivity(intent)
  }
  private fun goToCustomizeAlarmActivity() {
    val intent = Intent(requireContext(), CustomizeAlarmActivity::class.java)
    startActivity(intent)
  }
}

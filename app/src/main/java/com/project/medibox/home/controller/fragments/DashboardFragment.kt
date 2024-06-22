package com.project.medibox.home.controller.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.project.medibox.R
import com.project.medibox.medication.controller.activities.MedicationHistoryActivity
import com.project.medibox.medication.controller.activities.NewScheduleActivity
import com.project.medibox.pillboxmanagement.controller.activities.WiFiInstructionsActivity

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
    val cvDboardMedHistory = view.findViewById<CardView>(R.id.cvDboardMedHistory)
    cvScheduleReminder.setOnClickListener {
      goToNewScheduleActivity()
    }
    ivWifiInstructions.setOnClickListener {
      goToWifiInstructionsActivity()
    }
    cvDboardMedHistory.setOnClickListener {
      goToMedicationHistoryActivity()
    }
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
}

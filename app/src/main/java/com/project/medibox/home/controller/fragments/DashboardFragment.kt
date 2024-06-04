package com.project.medibox.home.controller.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.project.medibox.R
import com.project.medibox.medication.controller.activities.NewScheduleActivity

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
    cvScheduleReminder.setOnClickListener {
      goToNewScheduleActivity()
    }
  }

  private fun goToNewScheduleActivity() {
    val intent = Intent(context, NewScheduleActivity::class.java)
    startActivity(intent)
  }
}

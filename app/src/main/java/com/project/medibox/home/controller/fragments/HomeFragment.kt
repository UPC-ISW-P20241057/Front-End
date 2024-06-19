package com.project.medibox.home.controller.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.medibox.R
import com.project.medibox.medication.adapter.CompletedReminderAlarmAdapter
import com.project.medibox.medication.adapter.MissedReminderAlarmAdapter
import com.project.medibox.medication.adapter.UpcomingReminderAlarmAdapter
import com.project.medibox.medication.controller.activities.MedicationAlarmActivity
import com.project.medibox.medication.models.CompletedReminderAlarm
import com.project.medibox.medication.models.MissedReminderAlarm
import com.project.medibox.medication.models.UpcomingReminderAlarm
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.OnItemClickListener
import com.project.medibox.shared.OnItemClickListener2
import com.project.medibox.shared.OnItemClickListener3
import com.project.medibox.shared.StateManager

class HomeFragment : Fragment(), OnItemClickListener<UpcomingReminderAlarm>, OnItemClickListener2<CompletedReminderAlarm>, OnItemClickListener3<MissedReminderAlarm> {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvHiUser = view.findViewById<TextView>(R.id.tvHiUser)
        tvHiUser.text = "Hi ${StateManager.loggedUser.name}"
        val rvReminderAlarms = view.findViewById<RecyclerView>(R.id.rvReminderAlarms)
        var upcomingAlarms = AppDatabase.getInstance(requireContext()).getUpcomingReminderAlarmDao().getAll()
        Log.d("Database", upcomingAlarms.toString())
        rvReminderAlarms.layoutManager = LinearLayoutManager(requireContext())
        rvReminderAlarms.adapter = UpcomingReminderAlarmAdapter(upcomingAlarms, this)

        /*if (savedInstanceState == null) {
            childFragmentManager.beginTransaction()
                .replace(R.id.flReminders, UpcomingFragment())
                .commit()
        }*/

        val cvUpcoming = view.findViewById<CardView>(R.id.cvUpcoming)
        val cvCompleted = view.findViewById<CardView>(R.id.cvCompleted)
        val cvMissed = view.findViewById<CardView>(R.id.cvMissed)
        cvUpcoming.setOnClickListener {
            cvUpcoming.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.dark_menu_purple))
            cvCompleted.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.menu_bar_background))
            cvMissed.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.menu_bar_background))
            upcomingAlarms = AppDatabase.getInstance(requireContext()).getUpcomingReminderAlarmDao().getAll()
            Log.d("Database", upcomingAlarms.toString())
            upcomingAlarms = AppDatabase.getInstance(requireContext()).getUpcomingReminderAlarmDao().getAll()
            rvReminderAlarms.layoutManager = LinearLayoutManager(requireContext())
            rvReminderAlarms.adapter = UpcomingReminderAlarmAdapter(upcomingAlarms, this)
        }
        cvCompleted.setOnClickListener {
            cvUpcoming.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.menu_bar_background))
            cvCompleted.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.dark_menu_purple))
            cvMissed.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.menu_bar_background))
            //navigateTo(CompletedFragment())
            val completedAlarms = AppDatabase.getInstance(requireContext()).getCompletedReminderAlarmDao().getAll()
            rvReminderAlarms.layoutManager = LinearLayoutManager(requireContext())
            rvReminderAlarms.adapter = CompletedReminderAlarmAdapter(completedAlarms, this)
        }
        cvMissed.setOnClickListener {
            cvUpcoming.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.menu_bar_background))
            cvCompleted.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.menu_bar_background))
            cvMissed.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.dark_menu_purple))
            //navigateTo(MissedFragment())
            val missedAlarms = AppDatabase.getInstance(requireContext()).getMissedReminderAlarmDao().getAll()
            rvReminderAlarms.layoutManager = LinearLayoutManager(requireContext())
            rvReminderAlarms.adapter = MissedReminderAlarmAdapter(missedAlarms, this)
        }
    }

    override fun onItemClicked(value: UpcomingReminderAlarm) {
        StateManager.selectedUpcomingAlarm = value
        val intent = Intent(requireContext(), MedicationAlarmActivity::class.java)
        startActivity(intent)
    }

    override fun onItemClicked2(value: CompletedReminderAlarm) {
        StateManager.selectedCompletedAlarm = value
    }

    override fun onItemClicked3(value: MissedReminderAlarm) {

    }

    /*private fun navigateTo(fragment: Fragment): Boolean {
        return childFragmentManager
            .beginTransaction()
            .replace(R.id.flReminders, fragment)
            .commit() > 0
    }*/

}
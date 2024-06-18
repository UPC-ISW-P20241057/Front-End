package com.project.medibox.home.controller.fragments

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.medibox.R
import com.project.medibox.medication.adapter.UpcomingReminderAlarmAdapter
import com.project.medibox.medication.controller.fragments.CompletedFragment
import com.project.medibox.medication.controller.fragments.MissedFragment
import com.project.medibox.medication.controller.fragments.UpcomingFragment
import com.project.medibox.medication.models.UpcomingReminderAlarm
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.OnItemClickListener
import com.project.medibox.shared.StateManager

class HomeFragment : Fragment(), OnItemClickListener<UpcomingReminderAlarm> {

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
        var alarms = AppDatabase.getInstance(requireContext()).getUpcomingReminderAlarmDao().getAll()
        Log.d("Database", alarms.toString())
        rvReminderAlarms.layoutManager = LinearLayoutManager(requireContext())
        rvReminderAlarms.adapter = UpcomingReminderAlarmAdapter(alarms, requireContext(), this)

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
            alarms = AppDatabase.getInstance(requireContext()).getUpcomingReminderAlarmDao().getAll()
            Log.d("Database", alarms.toString())
            rvReminderAlarms.layoutManager = LinearLayoutManager(requireContext())
            rvReminderAlarms.adapter = UpcomingReminderAlarmAdapter(alarms, requireContext(), this)
        }
        cvCompleted.setOnClickListener {
            cvUpcoming.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.menu_bar_background))
            cvCompleted.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.dark_menu_purple))
            cvMissed.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.menu_bar_background))
            //navigateTo(CompletedFragment())
        }
        cvMissed.setOnClickListener {
            cvUpcoming.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.menu_bar_background))
            cvCompleted.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.menu_bar_background))
            cvMissed.setCardBackgroundColor(ContextCompat.getColor(view.context, R.color.dark_menu_purple))
            //navigateTo(MissedFragment())
        }
    }

    override fun onItemClicked(value: UpcomingReminderAlarm) {

    }

    /*private fun navigateTo(fragment: Fragment): Boolean {
        return childFragmentManager
            .beginTransaction()
            .replace(R.id.flReminders, fragment)
            .commit() > 0
    }*/

}
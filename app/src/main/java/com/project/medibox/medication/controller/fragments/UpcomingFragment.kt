package com.project.medibox.medication.controller.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.medibox.R
import com.project.medibox.medication.adapter.UpcomingReminderAlarmAdapter
import com.project.medibox.medication.models.UpcomingReminderAlarm
import com.project.medibox.shared.AppDatabase
import com.project.medibox.shared.OnItemClickListener

class UpcomingFragment : Fragment(), OnItemClickListener<UpcomingReminderAlarm> {

    private lateinit var rvUpcomingAlarms: RecyclerView
    private lateinit var alarms: List<UpcomingReminderAlarm>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvUpcomingAlarms = view.findViewById(R.id.rvUpcomingAlarms)
        loadUpcomingAlarms()
    }

    private fun loadUpcomingAlarms() {
        alarms = AppDatabase.getInstance(requireContext()).getUpcomingReminderAlarmDao().getAll()
        Log.d("Database", alarms.toString())
        rvUpcomingAlarms.layoutManager = LinearLayoutManager(requireContext())
        rvUpcomingAlarms.adapter = UpcomingReminderAlarmAdapter(alarms, this)
    }

    override fun onItemClicked(value: UpcomingReminderAlarm) {

    }
}
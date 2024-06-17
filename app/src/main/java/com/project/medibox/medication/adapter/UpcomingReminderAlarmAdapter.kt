package com.project.medibox.medication.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.medibox.medication.models.UpcomingReminderAlarm
import com.project.medibox.shared.OnItemClickListener

class UpcomingReminderAlarmAdapter(private val alarms: List<UpcomingReminderAlarm>, private val context: Context, private val itemClickListener: OnItemClickListener<UpcomingReminderAlarm>)
    :RecyclerView.Adapter<UpcomingReminderAlarmAdapter.Prototype>(){
    class Prototype(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Prototype {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: Prototype, position: Int) {
        TODO("Not yet implemented")
    }
}
package com.project.medibox.medication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.medibox.R
import com.project.medibox.medication.models.UpcomingReminderAlarm
import com.project.medibox.shared.OnItemClickListener
import com.project.medibox.shared.SharedMethods

class UpcomingReminderAlarmAdapter(private val alarms: List<UpcomingReminderAlarm>, private val context: Context, private val itemClickListener: OnItemClickListener<UpcomingReminderAlarm>)
    :RecyclerView.Adapter<UpcomingReminderAlarmAdapter.Prototype>(){
    class Prototype(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val cvUpcomingMedicine = itemView.findViewById<CardView>(R.id.cvUpcomingMedicine)
        private val tvMedicineName = itemView.findViewById<TextView>(R.id.tvMedicineName)
        private val ivClock = itemView.findViewById<ImageView>(R.id.ivClock)
        private val tvTimeToTake = itemView.findViewById<TextView>(R.id.tvTimeToTake)

        fun bind(alarm: UpcomingReminderAlarm, itemClickListener: OnItemClickListener<UpcomingReminderAlarm>) {
            tvMedicineName.text = alarm.medicineName
            tvTimeToTake.text = "${alarm.activateDateString} ${SharedMethods.formatHourMinute12H(alarm.activateHour, alarm.activateMinute)}"
            cvUpcomingMedicine.setOnClickListener {
                itemClickListener.onItemClicked(alarm)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Prototype {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.prototype_upcoming_reminder_alarm, parent, false)
        return Prototype(view)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    override fun onBindViewHolder(holder: Prototype, position: Int) {
        holder.bind(alarms[position], itemClickListener)
    }
}
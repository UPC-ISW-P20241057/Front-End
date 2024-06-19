package com.project.medibox.medication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.medibox.R
import com.project.medibox.medication.models.CompletedReminderAlarm
import com.project.medibox.shared.OnItemClickListener2
import com.project.medibox.shared.SharedMethods

class CompletedReminderAlarmAdapter (private val alarms: List<CompletedReminderAlarm>, private val itemClickListener: OnItemClickListener2<CompletedReminderAlarm>)
    :RecyclerView.Adapter<CompletedReminderAlarmAdapter.Prototype>() {

    class Prototype(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val cvUpcomingMedicine = itemView.findViewById<CardView>(R.id.cvCompletedMedicine)
        private val tvMedicineName = itemView.findViewById<TextView>(R.id.tvMedicineName)
        private val tvTimeToTake = itemView.findViewById<TextView>(R.id.tvTimeToTake)

        fun bind(alarm: CompletedReminderAlarm, itemClickListener: OnItemClickListener2<CompletedReminderAlarm>) {
            tvMedicineName.text = alarm.medicineName
            tvTimeToTake.text = "${alarm.activateDateString} ${SharedMethods.formatHourMinute12H(alarm.activateHour, alarm.activateMinute)}"
            cvUpcomingMedicine.setOnClickListener {
                itemClickListener.onItemClicked2(alarm)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Prototype {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.prototype_completed_reminder_alarm, parent, false)
        return Prototype(view)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    override fun onBindViewHolder(holder: Prototype, position: Int) {
        holder.bind(alarms[position], itemClickListener)
    }
}
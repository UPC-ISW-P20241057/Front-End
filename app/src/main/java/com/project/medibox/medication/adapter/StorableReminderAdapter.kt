package com.project.medibox.medication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.medibox.R
import com.project.medibox.medication.adapter.CompletedReminderAlarmAdapter.Prototype
import com.project.medibox.medication.models.StorableReminder
import com.project.medibox.shared.OnItemClickListener

class StorableReminderAdapter(private val reminders: List<StorableReminder>, private val itemClickListener: OnItemClickListener<StorableReminder>)
    :RecyclerView.Adapter<StorableReminderAdapter.Prototype>(){
    class Prototype(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val tvStoreMedName = itemView.findViewById<TextView>(R.id.tvStoreMedName)
        private val tvStoreMedPeriod = itemView.findViewById<TextView>(R.id.tvStoreMedPeriod)
        private val cvStoreMedication = itemView.findViewById<CardView>(R.id.cvStoreMedication)

        fun bind(reminder: StorableReminder, itemClickListener: OnItemClickListener<StorableReminder>) {
            tvStoreMedName.text = reminder.medicineName
            tvStoreMedPeriod.text = "${reminder.createdDateString} - ${reminder.endDateString}"
            cvStoreMedication.setOnClickListener {
                itemClickListener.onItemClicked(reminder)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Prototype {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.prototype_storable_reminder, parent, false)
        return Prototype(view)
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    override fun onBindViewHolder(holder: Prototype, position: Int) {
        holder.bind(reminders[position], itemClickListener)
    }
}
package com.project.medibox.medication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.project.medibox.R
import com.project.medibox.medication.models.UpcomingReminderAlarm
import com.project.medibox.shared.OnItemClickListener
import com.project.medibox.shared.SharedMethods

class UpcomingReminderAlarmAdapter(private val alarms: List<UpcomingReminderAlarm>, private val itemClickListener: OnItemClickListener<UpcomingReminderAlarm>)
    :RecyclerView.Adapter<UpcomingReminderAlarmAdapter.Prototype>(){
    class Prototype(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val cvUpcomingMedicine = itemView.findViewById<CardView>(R.id.cvUpcomingMedicine)
        private val tvMedicineName = itemView.findViewById<TextView>(R.id.tvMedicineName)
        private val ivClock = itemView.findViewById<ImageView>(R.id.ivClock)
        private val tvTimeToTake = itemView.findViewById<TextView>(R.id.tvTimeToTake)

        @SuppressLint("ResourceAsColor")
        fun bind(alarm: UpcomingReminderAlarm, itemClickListener: OnItemClickListener<UpcomingReminderAlarm>) {
            tvMedicineName.text = alarm.medicineName
            tvTimeToTake.text = "${alarm.activateDateString} ${SharedMethods.formatHourMinute12H(alarm.activateHour, alarm.activateMinute)}"
            ivClock.setImageDrawable(null) // Limpia la imagen
            itemView.requestLayout()

            if (alarm.notified) {
                // Elemento notificado
                cvUpcomingMedicine.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.medibox_default))
                ivClock.setImageResource(R.mipmap.alarm_on)
                tvMedicineName.setTextColor(Color.WHITE)
                tvTimeToTake.setTextColor(Color.WHITE)
            } else {
                // Elemento no notificado
                cvUpcomingMedicine.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.menu_bar_background))
                ivClock.setImageResource(R.mipmap.alarm)
                tvMedicineName.setTextColor(Color.BLACK)
                tvTimeToTake.setTextColor(Color.BLACK)
            }

            cvUpcomingMedicine.setOnClickListener {
                itemClickListener.onItemClicked(alarm)
            }

            itemView.requestLayout()
            itemView.invalidate()
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
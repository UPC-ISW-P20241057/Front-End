package com.project.medibox.home.containers

import android.view.View
import android.widget.TextView
import com.kizitonwose.calendar.view.ViewContainer
import com.project.medibox.R

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.calendarDayText)
}
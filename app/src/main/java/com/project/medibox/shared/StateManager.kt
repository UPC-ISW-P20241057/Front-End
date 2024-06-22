package com.project.medibox.shared

import com.project.medibox.identitymanagement.models.User
import com.project.medibox.medication.models.CompletedReminderAlarm
import com.project.medibox.medication.models.HistoricalReminder
import com.project.medibox.medication.models.Medicine
import com.project.medibox.medication.models.MissedReminderAlarm
import com.project.medibox.medication.models.UpcomingReminderAlarm

object StateManager {
    lateinit var authToken: String
    var loggedUserId: Long = -1
    lateinit var loggedUser: User
    var selectedMedicine: Medicine? = null
    var isAlarmChannelCreated: Boolean = false
    lateinit var selectedUpcomingAlarm: UpcomingReminderAlarm
    lateinit var selectedCompletedAlarm: CompletedReminderAlarm
    lateinit var selectedMissedAlarm: MissedReminderAlarm
    lateinit var selectedHistoricalReminder: HistoricalReminder
}
package com.project.medibox.medication.controller.activities

import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.echo.holographlibrary.Bar
import com.echo.holographlibrary.BarGraph
import com.project.medibox.R
import com.project.medibox.shared.AppDatabase
import kotlin.math.roundToInt

class MedicationProgressActivity : AppCompatActivity() {
    private lateinit var graphBars: ArrayList<Bar>
    private lateinit var graphBarView: BarGraph
    private lateinit var hexChars: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_medication_progress)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        graphBarView = findViewById(R.id.progressBarGraph) as BarGraph
        graphBars = ArrayList()
        hexChars = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")
        makeGraph()
    }

    private fun makeGraph() {
        val completedAlarms = AppDatabase.getInstance(this).getCompletedReminderAlarmDao().getAll()

        val medicineNames = completedAlarms.flatMap { listOf(it.medicineName) }
        val uniqueMedicineNames = medicineNames.distinct()

        val colorList = ArrayList<String>()

        for(i in uniqueMedicineNames.indices) {
            val colorString = generateHexColor()
            if (!colorList.contains(colorString))
                colorList.add(colorString)
        }

        for(i in uniqueMedicineNames.indices) {
            val bar = Bar()
            bar.color = Color.parseColor(colorList[i])
            bar.name = uniqueMedicineNames[i]
            bar.value = completedAlarms.count { it.medicineName == uniqueMedicineNames[i] }.toFloat()
            graphBars.add(bar)
        }

        graphBarView.bars = graphBars
    }

    private fun generateHexColor(): String {
        var color = "#"
        for (i in 0..5) {
            color += hexChars[(Math.random() * 15).roundToInt()]
        }
        return color
    }
}
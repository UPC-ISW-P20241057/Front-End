package com.project.medibox.medication.controller.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.TableLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.alclabs.fasttablelayout.FastTableLayout
import com.echo.holographlibrary.Bar
import com.echo.holographlibrary.BarGraph
import com.project.medibox.R
import com.project.medibox.shared.AppDatabase
import kotlin.math.roundToInt

class MedicationProgressActivity : AppCompatActivity() {
    private lateinit var graphBars: ArrayList<Bar>
    private lateinit var graphBarView: BarGraph
    private lateinit var hexChars: Array<String>
    private lateinit var progressTable: TableLayout
    private lateinit var tableHeaders: Array<String>
    private lateinit var tableData: Array<Array<String>>

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
        progressTable = findViewById(R.id.progressTable)
        graphBars = ArrayList()
        hexChars = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F")
        tableHeaders = arrayOf(getString(R.string.medicine),
            getString(R.string.taken), getString(R.string.percentage_completed))
        makeGraphAndTable()
    }

    private fun makeGraphAndTable() {
        val completedAlarms = AppDatabase.getInstance(this).getCompletedReminderAlarmDao().getAll()
        val missedAlarms = AppDatabase.getInstance(this).getMissedReminderAlarmDao().getAll()

        val medicineNames = completedAlarms.flatMap { listOf(it.medicineName) }
        val uniqueMedicineNames = medicineNames.distinct()

        val colorList = ArrayList<String>()

        for(i in uniqueMedicineNames.indices) {
            val colorString = generateHexColor()
            if (!colorList.contains(colorString))
                colorList.add(colorString)
        }
        val data = ArrayList<Array<String>>()

        for(i in uniqueMedicineNames.indices) {
            val bar = Bar()
            val countOfCompleted = completedAlarms.count { it.medicineName == uniqueMedicineNames[i] }
            val countOfMissed = missedAlarms.count { it.medicineName == uniqueMedicineNames[i] }
            val total = countOfCompleted + countOfMissed
            val decCompleted = countOfCompleted.toFloat() / total.toFloat()
            val percentage = String.format("%.2f", decCompleted * 100)
            bar.color = Color.parseColor(colorList[i])
            bar.name = uniqueMedicineNames[i]
            bar.value = countOfCompleted.toFloat()
            graphBars.add(bar)
            data.add(arrayOf(uniqueMedicineNames[i], countOfCompleted.toString(), "$percentage%"))
        }

        tableData = data.toTypedArray()

        graphBarView.bars = graphBars

        val fastTable = FastTableLayout(this, progressTable, tableHeaders, tableData)
        fastTable.SET_DEAFULT_HEADER_BORDER = true
        fastTable.TABLE_TEXT_SIZE = 18F
        fastTable.setCustomBackgroundToHeaders(R.color.light_purple)
        fastTable.build()
    }

    private fun generateHexColor(): String {
        var color = "#"
        for (i in 0..5) {
            color += hexChars[(Math.random() * 15).roundToInt()]
        }
        return color
    }
}
package com.example.test

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class ChartActivity : AppCompatActivity() {
    private val test = ArrayList<PieEntry>()
    private val pieColors = ArrayList<Int>()
    private val chart: PieChart by lazy { findViewById(R.id.pieChart) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chart_screen)

        val editText: EditText = findViewById(R.id.inputNumber)
        val buttonNumber: Button = findViewById(R.id.buttonNumber)
        val buttonRandom: Button = findViewById(R.id.buttonRandom)
        val random_number = (0..100)

        buttonNumber.setOnClickListener {
            var number = editText.text.toString().toFloatOrNull()
            if (number == null || number !in 0.0..100.0) {
                Toast.makeText(this@ChartActivity, "입력값이 조건에 맞지 않습니다.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val result = number
                val elseResult = 100 - result

                test.clear()
                test.add(PieEntry(result, "result"))
                test.add(PieEntry(elseResult, "default"))

                setupChart()
            }
        }

        buttonRandom.setOnClickListener {
            val random = random_number.random().toFloat()
            val result = random
            val elseResult = 100 - result

            test.clear()
            test.add(PieEntry(result, "result"))
            test.add(PieEntry(elseResult, "default"))

            setupChart()
        }
    }

    private fun setupChart() {
        chart.setUsePercentValues(true)

        pieColors.clear()
        for (c in ColorTemplate.VORDIPLOM_COLORS) {
            pieColors.add(c)
        }
        for (c in ColorTemplate.JOYFUL_COLORS) {
            pieColors.add(c)
        }
        pieColors.add(ColorTemplate.getHoloBlue())

        val dataset = PieDataSet(test, "")
        dataset.apply {
            colors = pieColors
            valueTextColor = Color.BLACK
            valueTextSize = 16f
        }

        val pieData = PieData(dataset)
        chart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = "값"
            setEntryLabelColor(Color.BLACK)
            animateY(1400, Easing.EaseInQuad)
            animate()
        }
    }
}
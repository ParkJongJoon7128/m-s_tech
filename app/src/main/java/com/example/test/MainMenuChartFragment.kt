// MainMenuChartFragment.kt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.test.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class MainMenuChartFragment : Fragment() {

    private lateinit var filterChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_menu_chart, container, false)
        filterChart = view.findViewById(R.id.filter_chart)
        setupChart()
        
        return view
    }

    private fun setupChart() {
        val entries = listOf(
            BarEntry(1f, 2f),
//            BarEntry(2f, 4f),
//            BarEntry(3f, 6f),
//            BarEntry(4f, 8f),
//            BarEntry(5f, 10f)
        )

        val dataSet = BarDataSet(entries, "Example Data")
        val data = BarData(dataSet)

        filterChart.data = data
        filterChart.invalidate()
    }
}

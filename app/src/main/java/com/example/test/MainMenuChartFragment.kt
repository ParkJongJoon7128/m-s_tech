import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.test.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

// ChartScreen에 대한 기능 구현 파일
class MainMenuChartFragment : Fragment() {

    // BarChart(막대 차트)를 전역 변수로 선언
    private lateinit var filterChart: BarChart

    // 앱을 실행하여 ChartScreen 띄웠을때 레이아웃을 보여주는 기능
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_menu_chart, container, false)
        filterChart = view.findViewById(R.id.filter_chart)

        initBarChart(filterChart)
        setupChart()

        return view
    }

    // BarChart(막대 차트) 차트 내 디자인 부분
    private fun initBarChart(barChart: BarChart) {
        // 차트 회색 배경 설정 (default = false)
        barChart.setDrawGridBackground(false)
        // 막대 그림자 설정 (default = false)
        barChart.setDrawBarShadow(false)
        // 차트 테두리 설정 (default = false)
        barChart.setDrawBorders(false)

        val description = Description()
        // 오른쪽 하단 모서리 설명 레이블 텍스트 표시 (default = false)
        description.isEnabled = false
        barChart.description = description

        // X, Y 바의 애니메이션 효과
        barChart.animateY(1000)
        barChart.animateX(1000)

        // 바텀 좌표 값
        val xAxis: XAxis = barChart.xAxis
        // x축 위치 설정
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        // 그리드 선 수평 거리 설정
        xAxis.granularity = 1f
        // x축 텍스트 컬러 설정
        xAxis.textColor = Color.BLACK
        // x축 선 설정 (default = true)
        xAxis.setDrawAxisLine(false)
        // 격자선 설정 (default = true)
        xAxis.setDrawGridLines(false)
        // x축 title 글자 크기
        xAxis.textSize = 20f
        // X축의 레이블을 변경하는 부분입니다.
        val labels = listOf("", "필터1", "필터2", "필터3", "필터4")
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        val leftAxis: YAxis = barChart.axisLeft
        // 좌측 선 설정 (default = true)
        leftAxis.setDrawAxisLine(false)
        // 좌측 텍스트 컬러 설정
        leftAxis.textColor = Color.BLACK
        // y축 title 글자 크기
        leftAxis.textSize = 20f

        val rightAxis: YAxis = barChart.axisRight
        // 우측 선 설정 (default = true)
        rightAxis.setDrawAxisLine(false)
        // 우측 텍스트 컬러 설정
        rightAxis.textColor = Color.BLACK
        rightAxis.setDrawLabels(false)

        // 바차트의 타이틀
        val legend: Legend = barChart.legend
        // 범례 모양 설정 (default = 정사각형)
        legend.form = Legend.LegendForm.SQUARE
        // 타이틀 텍스트 사이즈 설정
        legend.textSize = 20f
        // 타이틀 텍스트 컬러 설정
        legend.textColor = Color.BLACK
        // 범례 위치 설정
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        // 범례 방향 설정
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        // 차트 내부 범례 위치하게 함 (default = false)
        legend.setDrawInside(false)
    }

    // BarChart(막대 차트) 수치적, 색감적 디자인 부분
    private fun setupChart() {

        //임시 chart 데이터를 명시(x는 차트의 개수, y는 차트의 데이터 수치를 표현)
        val entries = listOf(
            BarEntry(1f, 2f),
            BarEntry(2f, 4f),
            BarEntry(3f, 6f),
            BarEntry(4f, 8f),
        )

        //차트 제목
        val title = "필터 청정도"

        //차트에 데이터 연결
        val dataSet = BarDataSet(entries, title)

        // 막대 차트 색 지정 및 막대 차트 name 크기 조정
        val colors = listOf(Color.YELLOW, Color.GREEN, Color.CYAN, Color.MAGENTA)
        dataSet.colors = colors
        dataSet.valueTextSize = 20f

        //차트에 데이터 연결
        val data = BarData(dataSet)

        //차트에 데이터 넣기
        filterChart.data = data
        filterChart.invalidate()
    }
}

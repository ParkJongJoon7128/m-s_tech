package com.example.test

import MainMenuChartFragment
import MainMenuWebViewFragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

//화면 설정 및 BottomNavigation 의 대한 기능 구현 파일
class MainActivity : AppCompatActivity() {

    //Fragment 를 관리하기 위한 Manager 선언
    private val fragmentManager: FragmentManager = supportFragmentManager

    //Fragment 형태인 3개의 스크린을 명시
    private val fragmentChart: MainMenuChartFragment = MainMenuChartFragment()
    private val fragmentBLE: MainMenuBLEFragment = MainMenuBLEFragment()
    private val fragmentWebView: MainMenuWebViewFragment = MainMenuWebViewFragment()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //앱 실행시 초기 화면을 ChartScreen으로 설정
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.menu_frame_layout, fragmentChart).commitAllowingStateLoss()

        //BottomNavigation 을 이용하여 화면 이동 기능하겠다는 명시
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.menu_bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(ItemSelectedListener())

    }

    // BottomNavigation 의 각 tab 을 클릭했을시 화면 전환과 어떤 xml 레이아웃이 사용될건지 조건문(c언어 에서는 switch 와 같음)을 사용하여 스크린 선택함.
    private inner class ItemSelectedListener : BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()

            when (menuItem.itemId) {
                R.id.menu_chart -> transaction.replace(R.id.menu_frame_layout, fragmentChart).commitAllowingStateLoss()
                R.id.menu_ble -> transaction.replace(R.id.menu_frame_layout, fragmentBLE).commitAllowingStateLoss()
                R.id.menu_webview -> transaction.replace(R.id.menu_frame_layout, fragmentWebView).commitAllowingStateLoss()
            }

            return true
        }
    }
}
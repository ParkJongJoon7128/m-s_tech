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

class MainActivity : AppCompatActivity() {

    private val fragmentManager: FragmentManager = supportFragmentManager
    private val fragmentChart: MainMenuChartFragment = MainMenuChartFragment()
    private val fragmentBLE: MainMenuBLEFragment = MainMenuBLEFragment()
    private val fragmentWebView: MainMenuWebViewFragment = MainMenuWebViewFragment()


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.menu_frame_layout, fragmentChart).commitAllowingStateLoss()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.menu_bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(ItemSelectedListener())

    }

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
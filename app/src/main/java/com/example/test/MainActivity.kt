package com.example.test

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

//    private var bleGatt: BluetoothGatt? = null
//    private val serviceUUID = UUID.fromString("55e405d2-af9f-a98f-e54a-7dfe43535355")
//    private val characteristicUUID = UUID.fromString("16962447-c623-61ba-d94b-4d1e43535349")

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

//        val Ble_btn: Button = findViewById(R.id.Ble_btn)
//        val Webview_btn: Button = findViewById(R.id.Webview_btn)
////        val test_btn: Button = findViewById(R.id.test_btn)
//
//        Ble_btn.setOnClickListener {
//            val intent = Intent(this, BleActivity::class.java)
//            startActivity(intent)
//        }
//
//        Webview_btn.setOnClickListener {
//            val intent = Intent(this, WebviewActivity::class.java)
//            startActivity(intent)
//        }

//        test_btn.setOnClickListener {
//            val intent = Intent(this, TestActivity::class.java)
//            startActivity(intent)
//        }

//        val test_btn: Button = findViewById(R.id.test_btn)
//        test_btn.setOnClickListener {
//            val builder = AlertDialog.Builder(this)
//            val dialogView = layoutInflater.inflate(R.layout.dialog_testmanager, null)
//            val test_editText: EditText = dialogView.findViewById(R.id.test_editText)
//
//            builder.setView(dialogView).setPositiveButton("전송") { dialog, _ ->
//                val test_text = test_editText.text.toString()
//
//                val result = test_text.toByteArray()
//                val service = bleGatt?.getService(serviceUUID)
//                val characteristic = service?.getCharacteristic(characteristicUUID)
//
//                characteristic?.value = result
//                bleGatt?.writeCharacteristic(characteristic)
//
//                dialog.dismiss()
//
//            }.setNegativeButton("취소") { dialog, _ ->
//                dialog.dismiss()
//            }.show()
//        }
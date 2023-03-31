package com.example.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import app.akexorcist.bluetotohspp.library.BluetoothSPP

@SuppressLint("MissingPermission")
class BLE_Activity : AppCompatActivity() {

//    // bluetoothspp
//    private val bluetooth: BluetoothSPP = BluetoothSPP(this)
//
//    // Button
//    private val ble_connect_btn: Button = findViewById(R.id.ble_connectBtn)
//    private val ble_send_btn: Button = findViewById(R.id.ble_sendBtn)
//
//    // EditText
//    private val ble_ssid: EditText = findViewById(R.id.ble_ssid)
//    private val ble_pw: EditText = findViewById(R.id.ble_pw)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble)

        // bluetoothspp
        val bluetooth: BluetoothSPP = BluetoothSPP(this)

        // Button
        val ble_connect_btn: Button = findViewById(R.id.ble_connectBtn)
        val ble_send_btn: Button = findViewById(R.id.ble_sendBtn)

        // EditText
        val ble_ssid: EditText = findViewById(R.id.ble_ssid)
        val ble_pw: EditText = findViewById(R.id.ble_pw)
//
//        if(!bluetooth.isBluetoothAvailable) {
//            Toast.makeText(this, "블루투스 활용 불가", Toast.LENGTH_SHORT).show()
//            finish()
//        }
//
//        bluetooth.setOnDataReceivedListener {
//            data, message ->
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//        }
//
//        bluetooth.setBluetoothConnectionListener(object : BluetoothSPP.BluetoothConnectionListener {
//            override fun onDeviceConnected(name: String?, address: String?) {
//                Toast.makeText(this@BLE_Activity, "Connected to $name\n$address", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onDeviceDisconnected() {
//                Toast.makeText(this@BLE_Activity, "Connection lost", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onDeviceConnectionFailed() {
//                Toast.makeText(this@BLE_Activity, "Unable to connect", Toast.LENGTH_SHORT).show()
//            }
//        })
//
//        ble_connect_btn.setOnClickListener {
//            if(bluetooth.serviceState == BluetoothState.STATE_CONNECTED) {
//                bluetooth.disconnect()
//            } else {
//                val intent = Intent(applicationContext, DeviceList::class.java)
//                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE)
//            }
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        bluetooth.stopService()
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if(!bluetooth.isBluetoothEnabled) {
//            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT)
//        } else {
//            if(!bluetooth.isServiceAvailable) {
//                bluetooth.setupService()
//                bluetooth.startService(BluetoothState.DEVICE_OTHER)
//                setup()
//            }
//        }
//    }
//
//    private fun setup() {
//        ble_send_btn.setOnClickListener {
//            bluetooth.send(ble_ssid.toString(), true)
//            bluetooth.send(ble_pw.toString(), true)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
//            if(resultCode == Activity.RESULT_OK) {
//                bluetooth.connect(data)
//            }
//        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
//            if(resultCode == Activity.RESULT_OK) {
//                bluetooth.setupService()
//                bluetooth.startService(BluetoothState.DEVICE_OTHER)
//                setup()
//            }  else {
//                Toast.makeText(this, "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show()
//            }
//        }
    }
}
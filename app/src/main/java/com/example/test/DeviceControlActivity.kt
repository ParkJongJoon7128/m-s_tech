package com.example.test

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


private val TAG = "gattClienCallback"

class DeviceControlActivity(
    private val context: Context?, private var bluetoothGatt: BluetoothGatt?
) : AppCompatActivity() {
    private var device: BluetoothDevice? = null
    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.i(TAG, "Connected to GATT server.")
                    Log.i(
                        TAG,
                        "Attempting to start service discovery: ${bluetoothGatt?.discoverServices()}"
                    )
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i(TAG, "Disconnected to GATT server.")
                    disconnectGattServer()
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.i(TAG, "Connected to GATT_SUCCESS")
                    broadcastUpdate("Connected ${device?.name}")
                }
                else -> {
                    Log.i(TAG, "Device service discovery failed, status: ${status}")
                    broadcastUpdate("Fail Connect ${device?.name}")
                }
            }
        }

        private fun broadcastUpdate(str: String) {
            val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
                }
            }
            mHandler.obtainMessage().sendToTarget()
        }
    }
    @SuppressLint("MissingPermission")
    fun connectGatt(device: BluetoothDevice): BluetoothGatt? {
        this.device = device

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt =
                device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
        }
        return bluetoothGatt
    }

    @SuppressLint("MissingPermission")
    fun disconnectGattServer(): BluetoothGatt? {
        Log.d(TAG, "Closing Gatt connection")
        if (bluetoothGatt != null) {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()
            bluetoothGatt = null

            Log.i(TAG, "Disconnected to GATT server.")
            Toast.makeText(context, "Disconnected device", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(context, "기기와 연결되어있지 않습니다", Toast.LENGTH_SHORT).show()
        }

        return bluetoothGatt
    }
}

//        @SuppressLint("MissingPermission")
//        private fun disconnectGattServer() {
//            Log.d(TAG, "Closing Gatt connection")
//            if (bluetoothGatt != null) {
//                bluetoothGatt?.disconnect()
//                bluetoothGatt?.close()
//                bluetoothGatt = null
//            }
//        }
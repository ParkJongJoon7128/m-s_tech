package com.example.test

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast


private val TAG = "gattClienCallback"

class DeviceControlActivity(
    private val context: Context?,
    private var bluetoothGatt: BluetoothGatt?
) {
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
        @SuppressLint("MissingPermission")
        private fun disconnectGattServer() {
            Log.d(TAG, "Closing Gatt connection")
            if(bluetoothGatt != null) {
                bluetoothGatt?.disconnect()
                bluetoothGatt?.close()
                bluetoothGatt = null
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun connectGatt(device: BluetoothDevice): BluetoothGatt? {
        this.device = device

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        } else{
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
        }
        return bluetoothGatt
    }
}
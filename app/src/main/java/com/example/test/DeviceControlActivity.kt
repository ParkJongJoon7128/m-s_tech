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

// BLE 컨트롤에 대한 기능 구현 파일
class DeviceControlActivity(
    private val context: Context?, private var bluetoothGatt: BluetoothGatt?
) : AppCompatActivity() {
    private var device: BluetoothDevice? = null
    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")

        //BLE 하기 위한 GATT 서버를 열어줘야 되는데, GATT 서버에 대한 연결 유무 판단
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.i(TAG, "Connected to GATT server.")
                    Log.i(TAG, "Attempting to start service discovery: ${bluetoothGatt?.discoverServices()}")
                    gatt?.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.i(TAG, "Disconnected to GATT server.")
                    disconnectGattServer()
                }
            }
        }

        // 탐색한 bluetooth 기기중 연결했을시 결과값
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

        // onCharacteristicWrite 메서드 추가
        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristic written successfully")
            } else {
                Log.e(TAG, "Characteristic write unsuccessful, status: $status")
                disconnectGattServer()
            }
        }

        // onCharacteristicRead 메서드 추가
        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    // 읽어온 값을 로그로 확인합니다.
                    val value = characteristic?.value?.toString(Charsets.UTF_8)
                    Log.d(TAG, "Read characteristic value: $value")
                }
                else -> {
                    Log.d(TAG, "Characteristic read failed, status: $status")
                    disconnectGattServer()
                }
            }
        }

        //Toast 메세지 출력
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

    // 탐색한 bluetooth 기기중 원하는 기기 연결하는 함수
    @SuppressLint("MissingPermission")
    fun connectGatt(device: BluetoothDevice): BluetoothGatt? {
        this.device = device

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        } else {
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
        }
        return bluetoothGatt
    }

    // 연결한 bluetooth 기기 연결 해제 시키는 함수
    @SuppressLint("MissingPermission")
    fun disconnectGattServer(): BluetoothGatt? {
        Log.d(TAG, "Closing Gatt connection")
        if (bluetoothGatt != null) {
            bluetoothGatt?.disconnect()
            bluetoothGatt?.close()

            Log.i(TAG, "Disconnected to GATT server.")
            Toast.makeText(context, "Disconnected ${bluetoothGatt?.device?.name}", Toast.LENGTH_SHORT).show()

            bluetoothGatt = null
        } else{
            Toast.makeText(context, "기기와 연결되어있지 않습니다", Toast.LENGTH_SHORT).show()
        }

        return bluetoothGatt
    }
}
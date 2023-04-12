package com.example.test

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.LocaleConfig
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("MissingPermission")
class BleActivity : AppCompatActivity() {

    private val REQUEST_ENABLE_BT = 1
    private var bluetoothAdapter: BluetoothAdapter? = null

    private val REQUEST_PERMISSIONS_ALL = 2
    private val PERMISSIONS = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    private var scanning: Boolean = false
    private var deviceArr = ArrayList<BluetoothDevice>()
    private val SCAN_PERIOD = 1000
    private val handler = Handler()

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    private var bleGatt: BluetoothGatt? = null
    private var mContext: Context? = null

//    private var builder = AlertDialog.Builder(this)

//    private val wifiManager: WifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
//    private val wifiInfo = wifiManager.connectionInfo.ssid


    private val mLeScanCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.d("scanCallback", "BLE Scan Failed: ${errorCode}")
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results?.let {
                for (result in it) {
                    if (!deviceArr.contains(result.device) && result.device.name != null) {
                        deviceArr.add(result.device)
                    }
                }
            }
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                if (!deviceArr.contains(it.device) && it.device.name != null) {
                    deviceArr.add(it.device)
                }
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun scanDevice(state: Boolean) = if (state) {
        handler.postDelayed({
            scanning = false
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(mLeScanCallback)
        }, SCAN_PERIOD)
        scanning = true
        deviceArr.clear()
        bluetoothAdapter?.bluetoothLeScanner?.startScan(mLeScanCallback)
    } else {
        scanning = false
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(mLeScanCallback)
    }

    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_ALL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                } else {
                    requestPermissions(permissions, REQUEST_PERMISSIONS_ALL)
                    Toast.makeText(this, "Permissions must be granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble)

        mContext = this

        val bluetooth_button: ToggleButton = findViewById(R.id.bluetooth_btn)
        val scan_button: Button = findViewById(R.id.scan_button)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        viewManager = LinearLayoutManager(this)
        recyclerViewAdapter = RecyclerViewAdapter(deviceArr)
        recyclerViewAdapter.mListener = object : RecyclerViewAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                scanDevice(false)
                val device = deviceArr.get(position)
                bleGatt = DeviceControlActivity(mContext, bleGatt).connectGatt(device)

//                // Dialog 띄우는 코드 추가
                val builder = AlertDialog.Builder(mContext)
                val dialogView = layoutInflater.inflate(R.layout.dialog_wifimanager, null)
                val ble_ssid = dialogView.findViewById<EditText>(R.id.ble_ssid)
                val ble_pw = dialogView.findViewById<EditText>(R.id.ble_pw)

                val wifiManager = mContext?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                val ssid = wifiInfo.ssid.replace("\"", "")

                ble_ssid.setText(ssid)

                builder.setView(dialogView)
                    .setPositiveButton("연결") { dialog, _ ->
                        dialog.dismiss()
                        // 연결 확인 버튼을 누른 경우의 동작 추가
                    }
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                        // 취소 버튼을 누른 경우의 동작 추가
                    }
                builder.create().show()
            }
        }

        val recyclerView = findViewById<RecyclerView?>(R.id.recyclerView).apply {
            layoutManager = viewManager
            adapter = recyclerViewAdapter
        }

        if (bluetoothAdapter != null) {
            if (bluetoothAdapter?.isEnabled == false) {
                bluetooth_button.isChecked = true
                scan_button.isVisible = false
            } else {
                bluetooth_button.isChecked = false
                scan_button.isVisible = true
            }
        }

        bluetooth_button.setOnCheckedChangeListener { _, isChecked ->
            bluetoothOnOff()
            scan_button.visibility = if (scan_button.visibility == View.VISIBLE) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }

            if(scan_button.visibility == View.INVISIBLE) {
                scanDevice(false)
                deviceArr.clear()
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }

        scan_button.setOnClickListener { v: View? ->
            if (!hasPermissions(this, PERMISSIONS)) {
                requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS_ALL)
            }
            scanDevice(true)
        }
    }

    fun bluetoothOnOff() {
        if (bluetoothAdapter == null) {
            Log.d("bluetoothAdapter", "Device doesn't support Bluetooth")
        } else {
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                bluetoothAdapter?.disable()
            }
        }
    }

    class RecyclerViewAdapter(private val myDataset: ArrayList<BluetoothDevice>) :
        RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

        var mListener: OnItemClickListener? = null

        interface OnItemClickListener{
            fun onClick(view: View, position: Int)
        }

        class MyViewHolder(val linearView: LinearLayout) : RecyclerView.ViewHolder(linearView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val linearView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
                    as LinearLayout
            return MyViewHolder(linearView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val itemName: TextView = holder.linearView.findViewById(R.id.item_name)
            val itemAddress: TextView = holder.linearView.findViewById(R.id.item_address)
            itemName.text = myDataset[position].name
            itemAddress.text = myDataset[position].address
            if(mListener != null) {
                holder?.linearView?.setOnClickListener { v ->
                    mListener?.onClick(v, position)
                }
            }
        }

        override fun getItemCount(): Int = myDataset.size
    }
}

private fun Handler.postDelayed(function: () -> Unit, scanPeriod: Int) {}
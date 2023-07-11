package com.example.test

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.util.*

class MainMenuBLEFragment : Fragment() {
    private lateinit var mainActivity: MainActivity

    private lateinit var test_button: Button
    private lateinit var test_editText: EditText

    private lateinit var bluetooth_button: ToggleButton
    private lateinit var scan_button: Button
    private lateinit var disconnect_button: Button
    private lateinit var recyclerView: RecyclerView

    private val REQUEST_ENABLE_BT = 1
    private var bluetoothAdapter: BluetoothAdapter? = null

    private val REQUEST_PERMISSIONS_ALL = 2
    private val PERMISSIONS = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)

    private var scanning: Boolean = false
    private var deviceArr = ArrayList<BluetoothDevice>()
    private val SCAN_PERIOD = 1000
    private val handler = Handler()

    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerViewAdapter: BleActivity.RecyclerViewAdapter

    private var bleGatt: BluetoothGatt? = null
    private var mContext: Context? = null
    private val serviceUUID = UUID.fromString("55e405d2-af9f-a98f-e54a-7dfe43535355")
    private val characteristicUUID = UUID.fromString("16962447-c623-61ba-d94b-4d1e43535349")
    private val mLeScanCallback =
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP) object : ScanCallback() {
            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.d("scanCallback", "BLE Scan Failed: ${errorCode}")
            }

            @SuppressLint("MissingPermission")
            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
                results?.let {
                    for (result in it) {
//                         if (!deviceArr.contains(result.device) && result.device.name != null && result.device.name == "MnS_Tech") {
                        if (!deviceArr.contains(result.device) && result.device.name != null) {
                            deviceArr.add(result.device)
                        }
                    }
                }
            }

            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                result?.let {
//                     if (!deviceArr.contains(it.device) && it.device.name != null && it.device.name == "MnS_Tech") {
                    if (!deviceArr.contains(it.device) && it.device.name != null) {
                        deviceArr.add(it.device)
                    }
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }

    @SuppressLint("MissingPermission")
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
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_ALL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mainActivity, "Permissions granted!", Toast.LENGTH_SHORT).show()
                    bluetooth_button.isChecked = true
                    scan_button.isVisible = false
                    disconnect_button.isVisible = false
                    test_button.isVisible = false
                    test_editText.isVisible = false
                } else {
                    requestPermissions(permissions, REQUEST_PERMISSIONS_ALL)
                    Toast.makeText(mainActivity, "Permissions must be granted", Toast.LENGTH_SHORT).show()
                    bluetooth_button.isChecked = true
                    scan_button.isVisible = false
                    disconnect_button.isVisible = false
                    test_button.isVisible = false
                    test_editText.isVisible = false
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = mainActivity

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        viewManager = LinearLayoutManager(mContext)
        recyclerViewAdapter = BleActivity.RecyclerViewAdapter(deviceArr)

        recyclerViewAdapter.mListener =
            object : BleActivity.RecyclerViewAdapter.OnItemClickListener {
                @SuppressLint("MissingPermission")
                override fun onClick(view: View, position: Int) {
                    scanDevice(false)
                    val device = deviceArr[position]
                    bleGatt = DeviceControlActivity(mContext, bleGatt).connectGatt(device)
                    if (bleGatt != null && bleGatt?.connect() == true) {
                        // Dialog 띄우는 코드 추가
                        val builder = AlertDialog.Builder(mContext)
                        val dialogView = layoutInflater.inflate(R.layout.dialog_wifimanager, null)
                        val ble_ssid = dialogView.findViewById<TextView>(R.id.ble_ssid)
                        val ble_pw = dialogView.findViewById<EditText>(R.id.ble_pw)

                        val wifiManager =
                            mContext?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
                        val wifiInfo = wifiManager.connectionInfo
                        val ssid = wifiInfo.ssid.replace("\"", "")

                        ble_ssid.text = ssid

                        builder.setView(dialogView).setPositiveButton("전송") { dialog, _ ->
                            // 연결 확인 버튼을 누른 경우의 동작 추가

                            val send_ssid = ble_ssid.text.toString()
                            val send_pw = ble_pw.text.toString()
                            val SP = ","
                            val CR = "\r"
                            val LF = "\n"

                            val sumData = send_ssid + SP + send_pw + CR + LF
                            val result = sumData.toByteArray()

                            val service = bleGatt?.getService(serviceUUID)
                            val characteristic = service?.getCharacteristic(characteristicUUID)

                            if(result.size <= 20){
                                characteristic?.value = result
                                bleGatt?.writeCharacteristic(characteristic)
                            } else{
                                val numPackets = (result.size + 19) / 20
                                for (i in 0 until numPackets) {
                                    val packetSize = if(i < numPackets - 1) {
                                        20
                                    } else{
                                        result.size % 20
                                    }
                                    val packet = result.copyOfRange(i * 20, i * 20 + packetSize)
                                    characteristic?.value = packet
                                    bleGatt?.writeCharacteristic(characteristic)
                                    Thread.sleep(10)
                                }
                            }
                            dialog.dismiss()
                        }.setNegativeButton("취소") { dialog, _ ->
                            // 취소 버튼을 누른 경우의 동작 추가
                            dialog.dismiss()
                        }
                        builder.show()
                    }
                }
            }
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_menu_b_l_e, container, false)
        test_button = view.findViewById(R.id.test_button)
        test_editText = view.findViewById(R.id.test_editText)

        test_button.setOnClickListener {
            try {
                if(test_editText.text.toString().isEmpty()) {
                    Toast.makeText(mainActivity, "값을 입력하고 버튼을 눌러주세요", Toast.LENGTH_SHORT).show()
                } else{
                    if (bleGatt != null && bleGatt?.connect() == true) {
                        val test_text = test_editText.text.toString()
                        val CR = "\r"
                        val LF = "\n"

                        val result = (test_text + CR + LF).toByteArray()

                        val service = bleGatt?.getService(serviceUUID)
                        val characteristic = service?.getCharacteristic(characteristicUUID)

                        if (result.size <= 20) { // 20바이트 이하일 때는 그대로 송신
                            characteristic?.value = result
                            bleGatt?.writeCharacteristic(characteristic)
                            Toast.makeText(mainActivity, test_editText.text.toString(), Toast.LENGTH_SHORT).show()
                            test_editText.text = null
                        } else { // 20바이트보다 크면 패킷으로 분할하여 여러 번 송신
                            val numPackets = (result.size + 19) / 20 // 전체 패킷 개수 계산
                            for (i in 0 until numPackets) { // 패킷 단위로 분할하여 여러 번 송신
                                val packetSize =
                                    if (i < numPackets - 1) 20 else result.size % 20 // 패킷 크기 계산
                                val packet = result.copyOfRange(i * 20, i * 20 + packetSize) // 패킷 복사
                                characteristic?.value = packet
                                bleGatt?.writeCharacteristic(characteristic)
                                Thread.sleep(10) // 패킷 간 간격을 두어 충돌을 방지합니다.
                            }
                            Toast.makeText(mainActivity, test_editText.text.toString(), Toast.LENGTH_SHORT).show()
                            test_editText.text = null
                        }
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(mainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        bluetooth_button = view.findViewById(R.id.bluetooth_btn)
        scan_button = view.findViewById(R.id.scan_button)
        disconnect_button = view.findViewById(R.id.disconnect_button)


        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = recyclerViewAdapter
        }

        if (bluetoothAdapter != null) {
            if (bluetoothAdapter?.isEnabled == false) {
                bluetooth_button.isChecked = true
                scan_button.isVisible = false
                disconnect_button.isVisible = false
                test_button.isVisible = false
                test_editText.isVisible = false
            } else {
                bluetooth_button.isChecked = false
                scan_button.isVisible = true
                disconnect_button.isVisible = true
                test_button.isVisible = true
                test_editText.isVisible = true
            }
        }

        bluetooth_button.setOnCheckedChangeListener { _, isChecked ->
            bluetoothOnOff()
            scan_button.visibility = if (scan_button.visibility == View.VISIBLE) {
                View.INVISIBLE
            } else {
                View.VISIBLE
            }

            disconnect_button.visibility = if(disconnect_button.visibility == View.VISIBLE){
                View.INVISIBLE
            } else{
                View.VISIBLE
            }

            test_button.visibility = if(test_button.visibility == View.VISIBLE){
                View.INVISIBLE
            } else{
                View.VISIBLE
            }

            test_editText.visibility = if(test_editText.visibility == View.VISIBLE) {
                View.INVISIBLE
            } else{
                View.VISIBLE
            }

            if (scan_button.visibility == View.INVISIBLE) {
                scanDevice(false)
                deviceArr.clear()
                recyclerViewAdapter.notifyDataSetChanged()
            }
        }

        scan_button.setOnClickListener { v: View? ->
            if (!hasPermissions(mainActivity, PERMISSIONS)) {
                requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS_ALL)
            }
            scanDevice(true)
        }

        disconnect_button.setOnClickListener {
            bleGatt = DeviceControlActivity(mContext, bleGatt).disconnectGattServer()
        }
    }

    @SuppressLint("MissingPermission")
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

    class RecyclerViewAdapter(private val myDataset: java.util.ArrayList<BluetoothDevice>) :
        RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

        var mListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onClick(view: View, position: Int)
        }

        class MyViewHolder(val linearView: LinearLayout) : RecyclerView.ViewHolder(linearView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val linearView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false) as LinearLayout
            return MyViewHolder(linearView)
        }

        @SuppressLint("MissingPermission")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val itemName: TextView = holder.linearView.findViewById(R.id.item_name)
            val itemAddress: TextView = holder.linearView.findViewById(R.id.item_address)
            itemName.text = myDataset[position].name
            itemAddress.text = myDataset[position].address
            if (mListener != null) {
                holder?.linearView?.setOnClickListener { v ->
                    mListener?.onClick(v, position)
                }
            }
        }

        override fun getItemCount(): Int = myDataset.size
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
}

private fun Handler.postDelayed(function: () -> Unit, scanPeriod: Int) {}
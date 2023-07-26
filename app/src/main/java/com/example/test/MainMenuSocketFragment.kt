package com.example.test

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket
import java.net.Socket


class MainMenuSocketFragment : Fragment() {

    private lateinit var serverStartBtn: Button
    private lateinit var serverEndBtn: Button
    private lateinit var socket_data_editText: EditText
    private lateinit var socket_data_Btn: Button

    private var server: ServerSocket? = null
    private var socket: Socket? = null

    private lateinit var localContext: MainActivity

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_menu_socket, container, false)

        serverStartBtn = view.findViewById(R.id.serverStartBtn)
        serverEndBtn = view.findViewById(R.id.serverEndBtn)
        socket_data_editText = view.findViewById(R.id.socket_data_editText)
        socket_data_Btn = view.findViewById(R.id.socket_data_Btn)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        socket_data_editText.setText(getWifiIpAddress(localContext))

        serverStartBtn.setOnClickListener {

            Thread {
                try {
                    server = ServerSocket(7128)
                    Log.d("wifi_ip_test", "서버를 열었습니다.")
                    Log.d("wifi_ip_test", "사용자 접속 대기중...")

                    socket = server?.accept()

                    val InputData = socket?.getInputStream()
                    val dataInputStream = DataInputStream(InputData)

                    val InputStringData = dataInputStream.readUTF()

                    socket?.close()
                    server?.close()

                    Log.d("data", "단말기로부터 받은 데이터: $InputStringData")

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }

        serverEndBtn.setOnClickListener {
            Thread {
                try {
                    socket?.close()
                    server?.close()

                    server = null // Set the server to null after closing
                    Log.d("wifi_ip_test", "서버를 닫았습니다.")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }

        socket_data_Btn.setOnClickListener {
            SocketAsyncTask().execute()
        }
    }

    inner class SocketAsyncTask: AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
           try {
               socket = Socket(getWifiIpAddress(localContext), 7128)

               val output = socket?.getOutputStream()
               val dataOutputStream = DataOutputStream(output)

               dataOutputStream.writeUTF(socket_data_editText.text.toString())

               socket?.close()
           }catch (e: Exception) {
               e.printStackTrace()
           }
            return ""
        }
    }

    // localContext를 전역 변수로 사용할수 있게 명시
    override fun onAttach(context: Context) {
        super.onAttach(context)
        localContext = context as MainActivity
    }
}

// WIFI IP 주소 받아오는 기능
private fun getWifiIpAddress(context: Context): String {
    val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo = wifiManager.connectionInfo
    val ipAddress = wifiInfo.ipAddress

    // IP 주소 형식 변환
    val ipString = String.format(
        "%d.%d.%d.%d",
        ipAddress and 0xff,
        ipAddress shr 8 and 0xff,
        ipAddress shr 16 and 0xff,
        ipAddress shr 24 and 0xff
    )
    return ipString
}
package com.example.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import java.io.DataInputStream
import java.net.ServerSocket
import java.net.Socket


class MainMenuSocketFragment : Fragment() {

    private lateinit var serverStartBtn: Button
    private lateinit var serverEndBtn: Button
    private var server: ServerSocket? = null
    private var socket: Socket? = null



    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_menu_socket, container, false)

        serverStartBtn = view.findViewById(R.id.serverStartBtn)
        serverEndBtn = view.findViewById(R.id.serverEndBtn)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serverStartBtn.setOnClickListener {

            Thread {
                try {
                    server = ServerSocket(7128)
                    Log.d("wifi_ip_test", "서버를 열었습니다.")
                    Log.d("wifi_ip_test", "사용자 접속 대기중...")
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
    }
}
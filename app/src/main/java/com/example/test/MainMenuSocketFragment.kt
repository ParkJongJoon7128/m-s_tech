package com.example.test

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket


class MainMenuSocketFragment : Fragment() {

    private lateinit var serverBtn: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_main_menu_socket, container, false)

        serverBtn = view.findViewById(R.id.serverBtn)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serverBtn.setOnClickListener {

            Thread() {
                try {
                    val server = ServerSocket(7128)
                    println("사용자 접속 대기중...")

                    val socket = server.accept()

                    val input = socket.getInputStream()
                    val dataInputStream = DataInputStream(input)

                    val stringData = dataInputStream.readUTF()

                    println("Android 에서 받은 문자열 : $stringData")

                    socket.close()
                    server.close()



//                    val output = socket.getOutputStream()
//                    val dataOutputStream = DataOutputStream(output)
//
//                    dataOutputStream.writeInt(7)
//                    dataOutputStream.writeUTF("서버 문자열")

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }
}
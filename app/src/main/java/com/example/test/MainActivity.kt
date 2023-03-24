package com.example.test

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val Webview_btn: Button = findViewById(R.id.Webview_btn)
        val Wife_btn: Button = findViewById(R.id.Wifi_btn)

        Wife_btn.setOnClickListener {

            val intent = packageManager.getLaunchIntentForPackage("com.example.myapplication")
            if (intent != null) {
                startActivity(intent)
            } else {
                Toast.makeText(
                    getApplicationContext(),
                    "BLE 통신 앱이 설치 되지 않았습니다.",
                    Toast.LENGTH_SHORT
                ).show();
            }

        }

        Webview_btn.setOnClickListener {
            val intent = Intent(this, WebviewActivity::class.java)
            startActivity(intent)
        }
    }
}
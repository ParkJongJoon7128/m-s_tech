package com.example.test

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val Webview_btn: Button = findViewById(R.id.Webview_btn)
        val Wife_btn: Button = findViewById(R.id.Wifi_btn)

        Wife_btn.setOnClickListener {

            val intent = packageManager.getLaunchIntentForPackage("com.example.myapplication")
            if(intent != null) {
                startActivity(intent)
            } else {
                
            }

        }

        Webview_btn.setOnClickListener {
            val intent = Intent(this, WebviewActivity::class.java)
            startActivity(intent)
        }
    }
}
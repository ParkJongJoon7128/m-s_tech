package com.example.test

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val Webview_btn: Button = findViewById(R.id.Webview_btn)
        val Wife_btn: Button = findViewById(R.id.Wifi_btn)

        Wife_btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val file = File("path/to/AI-thinker test 1.0.1")
            val uri = Uri.fromFile(file)
            intent.setDataAndType(uri, "application/vnd.android.package-archive")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }

        Webview_btn.setOnClickListener {
            val intent = Intent(this, WebviewActivity::class.java)
            startActivity(intent)
        }
    }
}
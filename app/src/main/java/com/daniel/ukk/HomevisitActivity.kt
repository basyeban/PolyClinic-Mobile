package com.daniel.ukk

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class HomevisitActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homevisit)

        val hubhomevisit: LinearLayout = findViewById(R.id.telpon)
        hubhomevisit.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.me/6281317170505")
            startActivity(intent)
        }


        val hubigd: LinearLayout = findViewById(R.id.telpon2)
        hubigd.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("tel:(0271) 2937111 ")
            startActivity(intent)
        }

        val backhomevisit = findViewById<ImageButton>(R.id.back_homevisit)
        backhomevisit.setOnClickListener {
            finish()
        }
    }
}

package com.example.nhanne.chatmessage.registerlogin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.nhanne.chatmessage.R
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        button_next_start.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}

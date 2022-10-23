package com.example.absencemanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView

class RegisterActivity : AppCompatActivity() {
    private lateinit var login_tv: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        login_tv = findViewById(R.id.login_tv)
        login_tv.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}
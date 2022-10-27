package com.example.absencemanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView

class RegisterActivity : AppCompatActivity() {
    private lateinit var login_text_view: TextView
    private val semsters = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")
    private val branches = arrayOf("GI", "SV", "LAE", "ECO")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        login_text_view = findViewById(R.id.login_tv)
        login_text_view.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }
}
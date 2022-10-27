package com.example.absencemanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var register_tv: TextView
    private lateinit var email_et: TextInputEditText
    private lateinit var password_et: TextInputEditText

    //Firebase
    val database = Firebase.database
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //initiate views
        initViews()

        register_tv.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    fun initViews() {
        register_tv = findViewById(R.id.register_tv)
        email_et = findViewById(R.id.email_et)
        password_et = findViewById(R.id.password_et)
    }
}
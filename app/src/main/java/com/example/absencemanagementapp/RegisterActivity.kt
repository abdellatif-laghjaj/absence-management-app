package com.example.absencemanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {
    private lateinit var login_tv: TextView
    private lateinit var first_name_et: TextInputEditText
    private lateinit var last_name_et: TextInputEditText
    private lateinit var cin_et: TextInputEditText
    private lateinit var cne_et: TextInputEditText
    private lateinit var filiere_dropdown: AutoCompleteTextView
    private lateinit var semester_dropdown: AutoCompleteTextView
    private lateinit var email_et: TextInputEditText
    private lateinit var password_et: TextInputEditText
    private lateinit var confirm_password_et: TextInputEditText

    private val semsters = arrayOf("1", "2", "3", "4", "5", "6", "7", "8")
    private val branches = arrayOf("GI", "SV", "LAE", "ECO")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //init views
        initViews()

        login_tv.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }

    fun initViews() {
        login_tv = findViewById(R.id.login_tv)
        first_name_et = findViewById(R.id.first_name_et)
        last_name_et = findViewById(R.id.last_name_et)
        cin_et = findViewById(R.id.cin_et)
        cne_et = findViewById(R.id.cne_et)
        filiere_dropdown = findViewById(R.id.filiere_dropdown)
        semester_dropdown = findViewById(R.id.semester_dropdown)
        email_et = findViewById(R.id.email_et)
        password_et = findViewById(R.id.password_et)
        confirm_password_et = findViewById(R.id.confirm_password_et)
    }
}
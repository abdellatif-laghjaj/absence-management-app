package com.example.absencemanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.absencemanagementapp.models.Student
import com.example.absencemanagementapp.models.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var register_tv: TextView
    private lateinit var cin_et: TextInputEditText
    private lateinit var password_et: TextInputEditText
    private lateinit var login_btn: Button

    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //initiate views
        initViews()

        register_tv.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        login_btn.setOnClickListener {
            val cin = cin_et.text.toString().trim().uppercase(Locale.getDefault())
            val password = password_et.text.toString().trim()

            if (validateInputs()) {
                login(cin, password)
            }
        }
    }

    private fun login(cin: String, password: String) {
        database = FirebaseDatabase.getInstance()
        val ref = database.getReference("students")

        // get student by cin
        ref.child(cin).get().addOnSuccessListener {
            if (it.exists()) {
                val user = it.getValue(Student::class.java)
                Log.d("TAG", "login: $user")
                if (user != null) {
                    if (user.password == password) {
                        Intent(this, MainActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() {
        register_tv = findViewById(R.id.register_tv)
        cin_et = findViewById(R.id.cin_et)
        password_et = findViewById(R.id.password_et)
        login_btn = findViewById(R.id.login_btn)
    }

    private fun validateInputs(): Boolean {
        val email = cin_et.text.toString()
        val password = password_et.text.toString()
        return when {
            email.isEmpty() -> {
                cin_et.error = "Email is required"
                false
            }
            password.isEmpty() -> {
                password_et.error = "Password is required"
                false
            }
            else -> true
        }
    }
}
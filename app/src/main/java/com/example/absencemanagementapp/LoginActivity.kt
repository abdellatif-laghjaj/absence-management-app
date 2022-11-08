package com.example.absencemanagementapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.absencemanagementapp.helpers.Helper.Companion.checkInternetConnection
import com.example.absencemanagementapp.helpers.Helper.Companion.isConnected
import com.example.absencemanagementapp.models.Student
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var register_tv: TextView
    private lateinit var email_et: TextInputEditText
    private lateinit var password_et: TextInputEditText
    private lateinit var login_btn: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

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
            val email = email_et.text.toString().trim().uppercase(Locale.getDefault())
            val password = password_et.text.toString().trim()

            if (validateInputs()) {
                login(email, password)
            }
        }
    }

    private fun login(email: String, password: String) {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        //log in the user
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = auth.currentUser
                //check if logged in user is a student or a teacher
                database.getReference("students").child(user!!.uid).get().addOnSuccessListener {
                    if (it.exists()) {
                        //user is a student
                        val student = it.getValue(Student::class.java)
                        Intent(this, StudentActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    } else {
                        //user is a teacher
                        Intent(this, TeacherActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    }
                }
            } else {
                // If sign in fails, display a dialog to the user.
                MaterialDialog.Builder(this)
                    .setTitle("Login Failed")
                    .setMessage("Invalid email or password")
                    .setCancelable(false)
                    .setPositiveButton("Ok") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .build()
                    .show()
            }
        }
    }

    private fun initViews() {
        register_tv = findViewById(R.id.register_tv)
        email_et = findViewById(R.id.email_et)
        password_et = findViewById(R.id.password_et)
        login_btn = findViewById(R.id.login_btn)
    }

    private fun validateInputs(): Boolean {
        val email = email_et.text.toString()
        val password = password_et.text.toString()
        return when {
            email.isEmpty() -> {
                email_et.error = "Email is required"
                email_et.requestFocus()
                false
            }
            password.isEmpty() -> {
                password_et.error = "Password is required"
                password_et.requestFocus()
                false
            }
            else -> true
        }
    }

    override fun onStart() {
        super.onStart()
        //check if user is connected to the internet
        if (!isConnected(this)) {
            MaterialDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again")
                .setCancelable(false)
                .setPositiveButton("Ok") { _, _ ->
                    checkInternetConnection(this, this)
                }
                .build()
                .show()
        }
    }
}
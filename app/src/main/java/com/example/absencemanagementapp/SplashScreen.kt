package com.example.absencemanagementapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val SPLASH_TIME_OUT: Long = 3000 // 3 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        //make full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //go to login activity after 3 seconds
        val handler = Handler()
        handler.postDelayed(Runnable {
            //skip login if user is already logged in
            if (auth.currentUser != null) {
                val user_id = auth.currentUser!!.uid

                //check if user is student or teacher
                database.getReference("students").child(user_id).get().addOnSuccessListener {
                    if (it.exists()) {
                        Intent(this, StudentActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    } else {
                        Intent(this, TeacherActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    }
                }
            } else {
                Intent(this, LoginActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }

        }, SPLASH_TIME_OUT)
    }
}
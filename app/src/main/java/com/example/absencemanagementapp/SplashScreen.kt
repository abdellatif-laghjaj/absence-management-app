package com.example.absencemanagementapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 3000 // 3 sec
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //make full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //go to login activity after 3 seconds
        val handler = Handler()
        handler.postDelayed(Runnable {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }, SPLASH_TIME_OUT)
    }
}
package com.example.absencemanagementapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.airbnb.lottie.LottieAnimationView
import com.example.absencemanagementapp.activities.auth.LoginActivity
import com.example.absencemanagementapp.activities.home.StudentActivity
import com.example.absencemanagementapp.activities.home.TeacherActivity
import com.example.absencemanagementapp.helpers.Helper
import com.example.absencemanagementapp.helpers.Helper.Companion.isConnected
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dev.shreyaspatil.MaterialDialog.MaterialDialog

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
                //check if user connected to the internet

                if (!isConnected(this)) {
                    val dialog_not_internet = MaterialDialog.Builder(this)
                        .setTitle("No Internet Connection")
                        .setAnimation(R.raw.no_internet)
                        .setMessage("Please check your internet connection and try again")
                        .setNegativeButton("Exit") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            finish()
                        }
                        .setPositiveButton("Ok") { _, _ ->
                            Helper.checkInternetConnection(this, this)
                        }
                        .build()
                    dialog_not_internet.show()

                    val animationView: LottieAnimationView = dialog_not_internet.getAnimationView()

                    //scale animation view
                    animationView.scaleX = 0.5f
                    animationView.scaleY = 0.5f
                }

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
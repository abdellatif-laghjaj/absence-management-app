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
import com.example.absencemanagementapp.helpers.Helper.Companion.changeLanguage
import com.example.absencemanagementapp.helpers.Helper.Companion.changeTheme
import com.example.absencemanagementapp.helpers.Helper.Companion.checkInternetConnection
import com.example.absencemanagementapp.helpers.Helper.Companion.isConnected
import com.example.absencemanagementapp.helpers.Helper.Companion.loadLanguage
import com.example.absencemanagementapp.helpers.Helper.Companion.loadTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dev.shreyaspatil.MaterialDialog.MaterialDialog

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val SPLASH_TIME_OUT: Long = 2500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //get theme and language from shared preferences
        val theme = loadTheme(this)
        val language = loadLanguage(this)
        //change theme and language
        changeTheme(theme, this)
        changeLanguage(language, this)

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
                        .setTitle(getString(R.string.no_internet_connection))
                        .setAnimation(R.raw.no_internet)
                        .setMessage(getString(R.string.please_check_your_connection))
                        .setNegativeButton(getString(R.string.exit)) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            finish()
                        }
                        .setPositiveButton(getString(R.string.ok)) { _, _ ->
                            checkInternetConnection(this, this)
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
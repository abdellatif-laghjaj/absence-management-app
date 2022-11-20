package com.example.absencemanagementapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.absencemanagementapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView

class SettingsActivity : AppCompatActivity() {
    private lateinit var swipe_refresh_layout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    private lateinit var student_image_civ: CircleImageView
    private lateinit var user_name_tv: TextView
    private lateinit var scan_qr_code_cv: CardView
    private lateinit var profile_cv: CardView
    private lateinit var reset_password_cv: CardView
    private lateinit var logout_cv: CardView
    private lateinit var back_iv: ImageView
    private lateinit var bottom_navigation: BottomNavigationView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //put the code here
        initViews()

        //set dashboard selected
        bottom_navigation.selectedItemId = R.id.profile
        //set bottom navigation listener
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard -> {
                    startActivity(Intent(this, StudentActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, StudentProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.settings -> {
                    true
                }
                else -> false
            }
        }

        back_iv.setOnClickListener {
            finish()
        }
    }

    //redirect to login activity
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initViews() {
        bottom_navigation = findViewById(R.id.bottom_navigation)
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout)
        student_image_civ = findViewById(R.id.student_image_civ)
        user_name_tv = findViewById(R.id.user_name_tv)
        logout_cv = findViewById(R.id.logout_cv)
        scan_qr_code_cv = findViewById(R.id.scan_qr_code_cv)
        profile_cv = findViewById(R.id.profile_cv)
        back_iv = findViewById(R.id.back_iv)
        reset_password_cv = findViewById(R.id.reset_password_cv)
    }
}
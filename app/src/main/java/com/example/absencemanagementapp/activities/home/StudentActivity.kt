package com.example.absencemanagementapp.activities.home

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.activities.qrcode.ScanQrCodeActivity
import com.example.absencemanagementapp.activities.auth.LoginActivity
import com.example.absencemanagementapp.activities.profile.StudentProfileActivity
import com.example.absencemanagementapp.activities.settings.StudentSettingsActivity
import com.example.absencemanagementapp.models.Student
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import de.hdodenhof.circleimageview.CircleImageView

class StudentActivity : AppCompatActivity() {
    private lateinit var swipe_refresh_layout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout
    private lateinit var student_image_civ: CircleImageView
    private lateinit var user_name_tv: TextView
    private lateinit var bottom_navigation: BottomNavigationView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        swipeToRefresh()

        //initiate views
        initViews()

        //set dashboard selected
        bottom_navigation.selectedItemId = R.id.dashboard
        //set bottom navigation listener
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard -> {
                    true
                }
                R.id.scan_qr_code -> {
                    startActivity(Intent(this, ScanQrCodeActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, StudentProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.settings -> {
                    startActivity(Intent(this, StudentSettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
        //get user name
        val user_id = auth.currentUser!!.uid
        database.getReference("students").child(user_id).get().addOnSuccessListener {
            if (it.exists()) {
                val student = it.getValue(Student::class.java)
                user_name_tv.text = student!!.first_name
            }
        }

        //get user image
        database.getReference("students").child(user_id).child("avatar").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    //get the image
                    val image = it.value.toString()
                    Glide.with(this).load(image).into(student_image_civ)
                }
            }

        student_image_civ.setOnClickListener {
            Intent(this, StudentProfileActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun swipeToRefresh() {
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout)
        swipe_refresh_layout.setOnRefreshListener {
            startActivity(Intent(this, StudentActivity::class.java))
            finish()
            overridePendingTransition(0, 0)
            swipe_refresh_layout.isRefreshing = false
        }

        //change the color of the swipe refresh layout
        swipe_refresh_layout.setColorSchemeResources(
            R.color.blue,
            R.color.white,
            R.color.yellow
        )
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
    }

    override fun onStart() {
        super.onStart()
        //ask for storage permission
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    //do nothing
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }
}
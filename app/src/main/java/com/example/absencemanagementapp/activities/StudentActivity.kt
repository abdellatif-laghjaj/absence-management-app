package com.example.absencemanagementapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dev.shreyaspatil.MaterialDialog.MaterialDialog

class StudentActivity : AppCompatActivity() {
    private lateinit var user_image_cv: CardView
    private lateinit var user_name_tv: TextView
    private lateinit var scan_qr_code_cv: CardView
    private lateinit var profile_cv: CardView
    private lateinit var reset_password_cv: CardView
    private lateinit var logout_cv: CardView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        //initiate views
        initViews()

        //get user name
        val user_id = auth.currentUser!!.uid
        database.getReference("students").child(user_id).get().addOnSuccessListener {
            if (it.exists()) {
                val student = it.getValue(Student::class.java)
                user_name_tv.text = student!!.first_name
            }
        }

        //dashboard cards handling
        logout_cv.setOnClickListener {
            logout()
        }

        profile_cv.setOnClickListener {
            Intent(this, StudentProfileActivity::class.java).also {
                startActivity(it)
            }
        }

        user_image_cv.setOnClickListener {
            Intent(this, StudentProfileActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    //logout
    private fun logout() {
        val dialog = MaterialDialog.Builder(this).setTitle("Logout")
            .setMessage("Are you sure you want to logout?").setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                redirectToLogin()
            }.setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.build()
        dialog.show()
    }

    //redirect to login activity
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initViews() {
        user_image_cv = findViewById(R.id.user_image_cv)
        user_name_tv = findViewById(R.id.user_name_tv)
        logout_cv = findViewById(R.id.logout_cv)
        //scan_qr_code_cv = findViewById(R.id.scan_qr_code_cv)
        profile_cv = findViewById(R.id.profile_cv)
        reset_password_cv = findViewById(R.id.reset_password_cv)
    }
}
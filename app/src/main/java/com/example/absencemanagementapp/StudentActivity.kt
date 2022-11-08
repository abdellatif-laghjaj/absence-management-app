package com.example.absencemanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.absencemanagementapp.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class StudentActivity : AppCompatActivity() {
    private lateinit var user_name_tv: TextView
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
            auth.signOut()
            //redirect to login activity
        }
    }

    private fun initViews() {
        user_name_tv = findViewById(R.id.user_name_tv)
        logout_cv = findViewById(R.id.logout_cv)
    }

    //logout
    private fun logout() {
        //alert
        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Are you sure?")
            .setContentText("You will be logged out!")
            .setConfirmText("Yes, logout!")
            .setConfirmClickListener { sDialog ->
                sDialog.dismissWithAnimation()
                auth.signOut()
                redirectToLogin()
            }
    }

    //redirect to login activity
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
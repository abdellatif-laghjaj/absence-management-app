package com.example.absencemanagementapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.absencemanagementapp.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var user_name_tv: TextView

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user_name_tv = findViewById(R.id.user_name_tv)
        //get current user
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        val user = auth.currentUser

        //get user id
        val userId = user?.uid
        //check if user is in student or teacher table
        database.getReference("students").child(userId!!).get().addOnSuccessListener {
            if (it.exists()) {
                val student = it.getValue(Student::class.java)
                user_name_tv.text = "${student?.first_name} ${student?.last_name}"
            } else {
                database.getReference("teachers").child(userId).get().addOnSuccessListener {
                    if (it.exists()) {
                        val teacher = it.getValue(Student::class.java)
                        user_name_tv.text = "${teacher?.first_name} ${teacher?.last_name}"
                    }
                }
            }
        }
    }
}
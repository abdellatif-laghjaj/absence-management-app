package com.example.absencemanagementapp.helpers

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}
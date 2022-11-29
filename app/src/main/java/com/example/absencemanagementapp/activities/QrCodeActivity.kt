package com.example.absencemanagementapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.absencemanagementapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shashank.sony.fancytoastlib.FancyToast

class QrCodeActivity : AppCompatActivity() {
    private lateinit var qr_code_iv: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private var id = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        qr_code_iv = findViewById(R.id.qr_code_iv)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        //get seance id from intent
        id = intent.getStringExtra("seance_id").toString()

        //get qr code
        getQrCodeImage()
    }

    //get qr code image from firebase storage
    private fun getQrCodeImage() {
        val storageRef = storage.reference
        val qrCodeRef = storageRef.child("qr_codes").child(id)
        qrCodeRef.downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(qr_code_iv)
        }.addOnFailureListener {
            FancyToast.makeText(
                this,
                "Error: ${it.message}",
                FancyToast.LENGTH_LONG,
                FancyToast.ERROR,
                false
            ).show()
        }
    }
}
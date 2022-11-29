package com.example.absencemanagementapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.absencemanagementapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shashank.sony.fancytoastlib.FancyToast

class QrCodeActivity : AppCompatActivity() {
    private lateinit var qr_code_iv: ImageView
    private lateinit var back_iv: ImageView
    private lateinit var share_btn: FloatingActionButton
    private lateinit var module_intitule_iv: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage

    private var seance_id = ""
    private var module_id = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        initView()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        //get seance id and module id from intent
        seance_id = intent.getStringExtra("seance_id").toString()
        module_id = intent.getIntExtra("module_id", -1).toString()

        //get qr code
        setQrCodeImage()
    }

    private fun initView() {
        qr_code_iv = this.findViewById(R.id.qr_code_iv)
        back_iv = this.findViewById(R.id.back_iv)
        share_btn = this.findViewById(R.id.share_btn)
        module_intitule_iv = this.findViewById(R.id.module_intitule_iv)

        back_iv.setOnClickListener { back() }
        share_btn.setOnClickListener { shareAction() }
    }

    //get qr code image from firebase storage
    private fun setQrCodeImage() {
        val storageRef = storage.reference
        val qrCodeRef = storageRef.child("qr_codes").child(module_id).child(seance_id)

        qrCodeRef.downloadUrl.addOnSuccessListener {
            Glide.with(this).load(it).into(qr_code_iv)
            println("qr code url ===> $it")
            qr_code_iv.setImageURI(it)
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

    private fun back() {
        TODO("not implemented yet!!")
    }

    private fun shareAction() {
        TODO("Not yet implemented")
    }
}
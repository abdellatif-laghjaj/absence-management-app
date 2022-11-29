package com.example.absencemanagementapp.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.absencemanagementapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shashank.sony.fancytoastlib.FancyToast
import com.squareup.picasso.Picasso
import java.net.URI

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
    private var module_intitule = ""
    private var url = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        initView()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        //get seance id and module id from intent
        seance_id = intent.getStringExtra("seance_id").toString()
        module_intitule = intent.getStringExtra("module_intitule").toString()
        module_id = intent.getIntExtra("module_id", -1).toString()
        url = intent.getStringExtra("url").toString()

        module_intitule_iv.text = module_intitule

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
        Picasso.with(this).load(url).into(qr_code_iv)
    }

    private fun back() {
        val intent = Intent(this, SeanceActivity::class.java)
        intent.putExtra("seance_id", seance_id)
        intent.putExtra("url", url)
        intent.putExtra("module_id", module_id)
        intent.putExtra("module_intitule", module_intitule)
        startActivity(intent)
    }

    private fun shareAction() {
//        TODO("Not yet implemented")
    }
}
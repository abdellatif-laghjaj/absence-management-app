package com.example.absencemanagementapp.activities

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.absencemanagementapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.shashank.sony.fancytoastlib.FancyToast
import com.squareup.picasso.Picasso
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors

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
        share_btn.setOnClickListener { saveQrCode() }
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

    private fun saveQrCode() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        var image: Bitmap?

        executor.execute {
            image = loadImage(url)
            handler.post {
                image?.let { saveMediaToStorage(it) }
            }
        }
    }

    private fun loadImage(string: String?): Bitmap? {
        var url: URL?
        val connection: HttpURLConnection?
        try {
            url = URL(string)
            try {
                connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val inputStream: InputStream = connection.inputStream
                val bufferedInputStream = BufferedInputStream(inputStream)
                connection.disconnect()
                return BitmapFactory.decodeStream(bufferedInputStream)
            } catch (e: IOException) {
                e.message?.let { Log.e("connection error", it) }
                FancyToast.makeText(
                    this,
                    "connection error",
                    FancyToast.LENGTH_LONG,
                    FancyToast.ERROR,
                    false
                ).show()
            }
        } catch (e: MalformedURLException) {
            url = null
            FancyToast.makeText(
                this,
                "problem in url!!",
                FancyToast.LENGTH_SHORT,
                FancyToast.ERROR,
                false
            ).show()
        }
        return null
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        val filename = "${module_intitule}_${seance_id}_${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            FancyToast.makeText(
                this,
                "QR Code saved to gallery",
                FancyToast.LENGTH_SHORT,
                FancyToast.SUCCESS,
                false
            ).show()
        }
    }
}
package com.example.absencemanagementapp.activities.qrcode

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
import com.airbnb.lottie.LottieAnimationView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.activities.seance.SeanceActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.shashank.sony.fancytoastlib.FancyToast
import com.squareup.picasso.Picasso
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Integer.parseInt
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
        module_id = intent.getIntExtra("module_id", -1).toString()
        url = intent.getStringExtra("url").toString()

        Log.e("debug", "qr activity ==> " + module_id)
        Log.e("debug", "qr activity get from intent ==> " + intent.getIntExtra("module_id", 404))

        setCurrentModuleIntitule(module_id)

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

    private fun setCurrentModuleIntitule(module_id: String) {
        database.getReference("modules").child(module_id).child("intitule").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    module_intitule_iv.text = it.value.toString()
                    module_intitule = it.value.toString()
                } else {
                    module_intitule_iv.text = "module intitule"
                }
            }
    }

    //get qr code image from firebase storage
    private fun setQrCodeImage() {
        Picasso.with(this).load(url).into(qr_code_iv)
    }

    private fun back() {
        val intent = Intent(this, SeanceActivity::class.java)
        intent.putExtra("seance_id", seance_id)
        intent.putExtra("url", url)
        intent.putExtra("module_id", parseInt(module_id))
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
                    getString(R.string.connection_error),
                    FancyToast.LENGTH_LONG,
                    FancyToast.ERROR,
                    false
                ).show()
            }
        } catch (e: MalformedURLException) {
            url = null
            FancyToast.makeText(
                this,
                getString(R.string.problem_url),
                FancyToast.LENGTH_SHORT,
                FancyToast.ERROR,
                false
            ).show()
        }
        return null
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        Dexter.withContext(this)
            .withPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : com.karumi.dexter.listener.multi.MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val filename =
                            "${module_intitule}_${seance_id}_${System.currentTimeMillis()}.jpg"
                        var fos: OutputStream? = null
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            contentResolver?.also { resolver ->
                                val contentValues = ContentValues().apply {
                                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                                    put(
                                        MediaStore.MediaColumns.RELATIVE_PATH,
                                        Environment.DIRECTORY_PICTURES
                                    )
                                }
                                val imageUri: Uri? =
                                    resolver.insert(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        contentValues
                                    )
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
                            val save_dialog =
                                MaterialDialog.Builder(this@QrCodeActivity)
                                    .setTitle(getString(R.string.success))
                                    .setAnimation(R.raw.saved)
                                    .setMessage(getString(R.string.qr_code_saved_to_gallery))
                                    .setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                    }.build()
                            save_dialog.show()

                            //scale animation
                            val animationView: LottieAnimationView = save_dialog.animationView
                            animationView.scaleX = 0.5f
                            animationView.scaleY = 0.5f
                        }
                    } else {
                        FancyToast.makeText(
                            this@QrCodeActivity,
                            getString(R.string.permission_denied),
                            FancyToast.LENGTH_SHORT,
                            FancyToast.ERROR,
                            false
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }
}
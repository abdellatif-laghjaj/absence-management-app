package com.example.absencemanagementapp.activities

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.models.Absence
import com.google.android.material.slider.Slider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.shashank.sony.fancytoastlib.FancyToast

class ScanQrCodeActivity : AppCompatActivity() {
    private lateinit var code_scanner: CodeScanner
    private lateinit var scanner_view: CodeScannerView
    private lateinit var zoom_slider: Slider
    private final val CAMERA_REQUEST_CODE = 101

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr_code_acticty)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        scanner_view = findViewById(R.id.scanner_view)
        zoom_slider = findViewById(R.id.zoom_slider)

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        } else {
            //start scanning process
            startScanning()
        }
    }

    private fun startScanning() {
        code_scanner = CodeScanner(this, scanner_view)
        code_scanner.camera = CodeScanner.CAMERA_BACK
        code_scanner.formats = CodeScanner.ALL_FORMATS

        code_scanner.autoFocusMode = AutoFocusMode.SAFE
        code_scanner.scanMode = ScanMode.SINGLE
        code_scanner.isAutoFocusEnabled = false
        code_scanner.isFlashEnabled = false

        //add zoom feature
        zoom_slider.addOnChangeListener { slider, value, fromUser ->
            code_scanner.zoom = value.toInt() * 5
            slider.value = value.toInt().toFloat()
        }

        code_scanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                //mark current student as present
                markStudentAsPresent(it.text)

                FancyToast.makeText(
                    this,
                    "Scanned Result: ${it.text}",
                    FancyToast.LENGTH_LONG,
                    FancyToast.SUCCESS,
                    false
                ).show()
            }
        }

        code_scanner.errorCallback = ErrorCallback {
            runOnUiThread {
                FancyToast.makeText(
                    this,
                    "Camera initialization error: ${it.message}",
                    FancyToast.LENGTH_LONG,
                    FancyToast.ERROR,
                    false
                ).show()
            }
        }

        //start scanning
        scanner_view.setOnClickListener {
            code_scanner.startPreview()
        }
    }

    private fun markStudentAsPresent(seance_id: String) {
        val ref = database.getReference("absences")
        val absence = Absence()
        val id = ref.push().key
        absence.cne = getStudentCne()
        absence.seance_id = seance_id
        absence.is_present = true

        ref.child(id!!).setValue(absence)
    }

    private fun getStudentCne(): String {
        val ref = database.getReference("students")
        val cne = ref.child(auth.currentUser!!.uid).child("cne").get().toString()
        return cne
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                startScanning()
            } else {
                //permission denied
                FancyToast.makeText(
                    this,
                    "Camera permission is required to use this feature",
                    FancyToast.LENGTH_LONG,
                    FancyToast.ERROR,
                    false
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::code_scanner.isInitialized) {
            code_scanner.startPreview()
        }
    }

    override fun onPause() {
        if (::code_scanner.isInitialized) {
            code_scanner.releaseResources()
        }
        super.onPause()
    }
}
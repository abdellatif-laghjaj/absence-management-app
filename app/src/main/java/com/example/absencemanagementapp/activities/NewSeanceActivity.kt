package com.example.absencemanagementapp.activities

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.helpers.Helper.Companion.formatSeanceId
import com.example.absencemanagementapp.models.Seance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.shashank.sony.fancytoastlib.FancyToast
import java.io.ByteArrayOutputStream
import java.util.*

class NewSeanceActivity : AppCompatActivity() {
    private lateinit var back_iv: ImageView

    private lateinit var type_dropdown: AutoCompleteTextView
    private lateinit var seance_date: AutoCompleteTextView
    private lateinit var start_dropdown: AutoCompleteTextView
    private lateinit var end_dropdown: AutoCompleteTextView
    private lateinit var salle_dropdown: AutoCompleteTextView

    private lateinit var add_seance_btn: AppCompatButton
    private lateinit var seance: Seance

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    var id = 0

    var types = arrayOf("Cours", "TP", "Exam")
    var startHours = arrayOf("08:30", "10:30", "12:30", "14:30", "16:30")
    var endHours = arrayOf("10:15", "12:15", "14:15", "16:15", "18:15")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_seance)
        id = intent.getIntExtra("id", 0)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()


        //initiate views
        initViews()


        salle_dropdown.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, getLocales()))
        type_dropdown.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, types))

        seance_date.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val date_picker_dialog = DatePickerDialog(
                this, { view, selected_year, selected_month, selected_day ->
                    seance_date.setText("$selected_day/${selected_month + 1}/$selected_year")
                },
                year,
                month,
                day
            )
            date_picker_dialog.show()
        }
        start_dropdown.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, startHours))
        end_dropdown.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, endHours))

        add_seance_btn.setOnClickListener {
            if (validateInputs()) {
                addNewSeance()
            }
        }
    }

    private fun initViews() {
        back_iv = this.findViewById(R.id.back_iv)
        back_iv.setOnClickListener({ back() })
        type_dropdown = this.findViewById(R.id.type_dropdown)
        seance_date = this.findViewById(R.id.seance_date)
        start_dropdown = this.findViewById(R.id.start_dropdown)
        end_dropdown = this.findViewById(R.id.end_dropdown)
        salle_dropdown = this.findViewById(R.id.salle_dropdown)
        add_seance_btn = this.findViewById(R.id.add_seance_btn)
    }

    private fun addNewSeance() {
        //create new seance
        seance = Seance()

        seance.type = type_dropdown.text.toString()
        seance.date = seance_date.text.toString()
        seance.start_time = start_dropdown.text.toString()
        seance.end_time = end_dropdown.text.toString()
        seance.n_salle = salle_dropdown.text.toString()
        seance.n_module = id

        //save seance to database
        saveSeance(seance)

        //generate qr code


//        //display qr code in dialog
//        val dialog = Dialog(this)
//        dialog.setContentView(R.layout.dialog_user_image)
//        val image = dialog.findViewById<ImageView>(R.id.user_image_iv)
//        image.setImageBitmap(bitmap)
//        dialog.window!!.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        dialog.window!!.attributes.windowAnimations = android.R.style.Animation_Dialog
//        dialog.show()
    }

    private fun generateQrCode(seance: Seance): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix =
            writer.encode(seance.toString(), BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix.get(
                            x,
                            y
                        )
                    ) Color.BLACK else Color.WHITE
                )
            }
        }

        return bitmap
    }

    private fun getLocales(): Array<String> {
        return arrayOf(
            "A1",
            "A2",
            "A3",
            "A4",
            "SL1",
            "SL2",
            "TD1",
            "TD2",
            "TD3",
            "M1",
            "M2",
            "M3",
            "Info1",
            "Info2",
            "Info3"
        )
    }

    private fun back() {
        intent = Intent(this, ModuleActivity::class.java)
        intent.putExtra("id", id)
        startActivity(intent)
        finish()
    }

    private fun validateInputs(): Boolean {
        return when {
            type_dropdown.text.toString().isEmpty() -> {
                type_dropdown.error = "Type is required"
                false
            }
            seance_date.text.toString().isEmpty() -> {
                seance_date.error = "Date is required"
                false
            }
            start_dropdown.text.toString().isEmpty() -> {
                start_dropdown.error = "Start time is required"
                false
            }
            end_dropdown.text.toString().isEmpty() -> {
                end_dropdown.error = "End time is required"
                false
            }
            salle_dropdown.text.toString().isEmpty() -> {
                salle_dropdown.error = "Salle is required"
                false
            }
            else -> true
        }
    }

    private fun storeQrCode(seance: Seance) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.setMessage("Please wait while we upload and process the image")
        progressDialog.show()

        val ref = storage.getReference("qr_codes")

//      convert to bytecode
        var baos = ByteArrayOutputStream()
        generateQrCode(seance).compress(Bitmap.CompressFormat.JPEG, 100, baos)

        ref.putBytes(baos.toByteArray())
            .addOnSuccessListener {
                progressDialog.dismiss()
                FancyToast.makeText(
                    this,
                    "Image uploaded successfully",
                    FancyToast.LENGTH_SHORT,
                    FancyToast.SUCCESS,
                    false
                ).show()
                val qrUrl = ref.downloadUrl.toString()
                seance.qrCodeUrl = qrUrl
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                FancyToast.makeText(
                    this,
                    "Failed to upload image",
                    FancyToast.LENGTH_SHORT,
                    FancyToast.ERROR,
                    false
                ).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress =
                    100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
                progressDialog.setMessage("Uploaded " + progress.toInt() + "%...")
            }
    }

    private fun saveSeance(seance: Seance) {
        //TODO: save seance to database
        val ref = database.getReference("seances")
        val id = ref.push().key
        seance.id = id
        storeQrCode(seance)
        if (id != null) {
            ref.child(id).setValue(seance)
            moveToQrCodeView()
        }
    }

    private fun moveToQrCodeView() {
//        TODO: not implemented yet
    }
}
package com.example.absencemanagementapp.activities.seance

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.activities.ModuleActivity
import com.example.absencemanagementapp.activities.qrcode.QrCodeActivity
import com.example.absencemanagementapp.models.Absence
import com.example.absencemanagementapp.models.Seance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.shashank.sony.fancytoastlib.FancyToast
import java.io.ByteArrayOutputStream
import java.lang.Integer.parseInt
import java.util.*
import kotlin.collections.ArrayList

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

    var module_id = ""

    var types = arrayOf("Cours", "TP", "Exam")
    var startHours = arrayOf("08:30", "10:30", "12:30", "14:30", "16:30")
    var endHours = arrayOf("10:15", "12:15", "14:15", "16:15", "18:15")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_seance)

        module_id = intent.getStringExtra("module_id").toString()

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
                }, year, month, day
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

    override fun onBackPressed() {
        back()
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
        seance.n_module = parseInt(module_id)

        //save seance to database
        saveSeance(seance)
    }

    private fun saveSeance(seance: Seance) {
        val ref = database.getReference("seances")
        val id = ref.push().key
        if (id != null) {
            seance.id = id.toString()
            ref.child(id).setValue(seance).addOnSuccessListener {
                markEveryOneAsAbsent(id.toString())
                storeQrCode(seance)
            }.addOnFailureListener {
                FancyToast.makeText(
                    this,
                    getString(R.string.failed_to_add_seance),
                    FancyToast.LENGTH_SHORT,
                    FancyToast.ERROR,
                    false
                ).show()
            }
        }
    }

    private fun storeQrCode(seance: Seance) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.generating_qr_code))
        progressDialog.setMessage(getString(R.string.please_wait))
        progressDialog.show()
        val ref = seance.id?.let {
            storage.getReference("qr_codes").child(module_id.toString()).child(it)
        }

        // convert to bytecode
        var baos = ByteArrayOutputStream()
        generateQrCode(seance).compress(Bitmap.CompressFormat.JPEG, 100, baos)

        if (ref != null) {
            ref.putBytes(baos.toByteArray()).addOnSuccessListener { task: UploadTask.TaskSnapshot ->
                if (task.task.isSuccessful) {
                    FancyToast.makeText(
                        this,
                        getString(R.string.qr_code_saved),
                        FancyToast.LENGTH_SHORT,
                        FancyToast.SUCCESS,
                        false
                    ).show()

                    progressDialog.dismiss()

                    updateQrCodeURL(module_id.toString(), seance.id!!)
                }
            }.addOnFailureListener {
                FancyToast.makeText(
                    this,
                    getString(R.string.failed_to_save_qr_code),
                    FancyToast.LENGTH_SHORT,
                    FancyToast.ERROR,
                    false
                ).show()

                progressDialog.dismiss()
            }
        }
    }

    private fun generateQrCode(seance: Seance): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(seance.id.toString(), BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x, y, if (bitMatrix.get(
                            x, y
                        )
                    ) Color.BLACK else Color.WHITE
                )
            }
        }

        return bitmap
    }

    private fun updateQrCodeURL(module_id: String, seance_id: String) {
        val qrCodeRef = storage.getReference("qr_codes").child(module_id).child(seance_id)

        qrCodeRef.downloadUrl.addOnSuccessListener {
            val seanceRef = database.getReference("seances").child(seance_id)
            seance.qrCodeUrl = it.toString()
            seanceRef.child("qrCodeUrl").setValue(it.toString())
            moveToQrCodeView(seance_id, it.toString())
        }.addOnFailureListener {
            FancyToast.makeText(
                this, "Error: ${it.message}", FancyToast.LENGTH_LONG, FancyToast.ERROR, false
            ).show()
        }
    }

    private fun moveToQrCodeView(id: String, url: String) {
        val intent = Intent(this, QrCodeActivity::class.java)
        intent.putExtra("seance_id", id)
        intent.putExtra("module_id", parseInt(module_id))
        intent.putExtra("url", url)
        startActivity(intent)
        finish()
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
        val intent = Intent(this, ModuleActivity::class.java)
        intent.putExtra("module_id", module_id)
        startActivity(intent)
        finish()
    }

    private fun validateInputs(): Boolean {
        return when {
            type_dropdown.text.toString().isEmpty() -> {
                type_dropdown.error = getString(R.string.type_required)
                false
            }
            seance_date.text.toString().isEmpty() -> {
                seance_date.error = getString(R.string.date_required)
                false
            }
            start_dropdown.text.toString().isEmpty() -> {
                start_dropdown.error = getString(R.string.start_time_required)
                false
            }
            end_dropdown.text.toString().isEmpty() -> {
                end_dropdown.error = getString(R.string.end_time_required)
                false
            }
            salle_dropdown.text.toString().isEmpty() -> {
                salle_dropdown.error = getString(R.string.salle_required)
                false
            }
            else -> true
        }
    }

    private fun markEveryOneAsAbsent(seance_id: String) {
//        get students list & mark everyone as absent
        database.getReference("inscriptions").get().addOnSuccessListener {
            for (ds in it.children) {
                if (ds.child("n_module").value.toString().equals(module_id)) {
                    val key = database.getReference("absences").push().key
                    val absence = Absence(key.toString(), ds.child("cne").value.toString(), seance_id, false)
                    database.getReference("absences/" + key).setValue(absence)
                }
            }
        }.addOnFailureListener {
            Log.e("debug", it.message.toString())
        }
    }
}

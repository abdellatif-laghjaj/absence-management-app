package com.example.absencemanagementapp.activities

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.activities.home.TeacherActivity
import com.example.absencemanagementapp.activities.seance.NewSeanceActivity
import com.example.absencemanagementapp.adapters.SeanceAdapter
import com.example.absencemanagementapp.models.Absence
import com.example.absencemanagementapp.models.Module
import com.example.absencemanagementapp.models.Seance
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.shashank.sony.fancytoastlib.FancyToast
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Integer.parseInt
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class ModuleActivity : AppCompatActivity() {
    private lateinit var module_name_tv: TextView
    private lateinit var back_iv: ImageView
    private lateinit var seances_swipe: SwipeRefreshLayout
    private lateinit var rv: RecyclerView
    private lateinit var absence_list_cv: MaterialCardView
    private lateinit var new_seance_cv: MaterialCardView

    private lateinit var dbRef: FirebaseDatabase

    private var currentModuleId: String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module)

        dbRef = FirebaseDatabase.getInstance()

        initView()

        getSeances()
    }

    override fun onBackPressed() {
        back()
    }

    private fun initSeances(seances: ArrayList<Seance>) {
        rv = findViewById<RecyclerView>(R.id.seances_rv)
        rv.layoutManager = LinearLayoutManager(this)
        val module = Module()
        val seanceAdapter = SeanceAdapter(seances, this, module)
        rv.adapter = seanceAdapter
    }

    private fun initView() {
        module_name_tv = this.findViewById(R.id.module_intitule_tv)
        back_iv = this.findViewById(R.id.back_iv)
        absence_list_cv = this.findViewById(R.id.absence_list_cv)
        new_seance_cv = this.findViewById(R.id.new_seance_cv)
        seances_swipe = this.findViewById(R.id.seances_swipe)

        currentModuleId = intent.getIntExtra("module_id", -1).toString()
        setCurrentModuleIntitule(currentModuleId)

        back_iv.setOnClickListener { back() }

        absence_list_cv.setOnClickListener { exportAbsencesToExcel() }

        new_seance_cv.setOnClickListener { toNewSeanceView() }

        seances_swipe.setOnRefreshListener {
            getSeances()
            seances_swipe.isRefreshing = false
        }
    }

    private fun setCurrentModuleIntitule(module_id: String) {
        dbRef.getReference("modules").child(module_id).child("intitule").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    module_name_tv.text = it.value.toString()
                } else {
                    module_name_tv.text = "module intitule"
                }
            }
    }

    private fun getSeances() {
        dbRef.getReference("seances").get()
            .addOnSuccessListener {
                val seances = ArrayList<Seance>()
                for (ds in it.children) {
                    if (ds.child("n_module").value.toString().equals(currentModuleId)) {
                        val id = ds.child("id").value.toString()
                        val date = ds.child("date").value.toString()
                        val start_time = ds.child("start_time").value.toString()
                        val end_time = ds.child("end_time").value.toString()
                        val type = ds.child("type").value.toString()
                        val n_salle = ds.child("n_salle").value.toString()
                        val n_module = parseInt(ds.child("n_module").value.toString())
                        val total_absences = parseInt(ds.child("total_absences").value.toString())
                        val qrCodeUrl = ds.child("qrCodeUrl").value.toString()
                        var seance = Seance(
                            id,
                            date,
                            start_time,
                            end_time,
                            type,
                            n_salle,
                            n_module,
                            total_absences,
                            qrCodeUrl
                        )
                        seances.add(seance)
                    }
                }
                initSeances(seances)
            }
    }

    private fun back() {
        val intent = Intent(this, TeacherActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toNewSeanceView() {
        val intent = Intent(this, NewSeanceActivity::class.java)
        intent.putExtra("module_id", currentModuleId)
        startActivity(intent)
        finish()
    }

    //export absence list to excel file
    private fun exportAbsencesToExcel() {
        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Absences")

        //get number of columns
        val columns = ArrayList<String>()

        columns.add("CNE")
        columns.add("Nom")
        columns.add("Prénom")

        dbRef.getReference("seances").get().addOnSuccessListener {
            for (ds in it.children) {
                val title: String = ds.child("date").value.toString() + " " + ds.child("start_time").value.toString() + " - " + ds.child("end_time").value.toString() + " (" + ds.child("type").value.toString() + ")"
                columns.add(title)
            }

            columns.add("Total")

            //create header row
            val headerRow = sheet.createRow(0)
            for (i in columns.indices) {
                val cell = headerRow.createCell(i)
                cell.setCellValue(columns[i].uppercase())
            }

            //add padding to header row and set column width
            val headerCellStyle = workbook.createCellStyle()
            headerCellStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            headerCellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
            for (i in columns.indices) {
                val cell = headerRow.getCell(i)
                cell.cellStyle = headerCellStyle
                sheet.setColumnWidth(i, 6000)
            }

            //get students
            dbRef.getReference("inscriptions").get().addOnSuccessListener {
                var rowIdx = 1
                val students = ArrayList<String>()
                for (ds in it.children) {
                    if (ds.child("n_module").value.toString().equals(currentModuleId)) {
                        val cne = ds.child("cne").value.toString()
                        students.add(cne)
                        val row = sheet.createRow(rowIdx++)
                        row.createCell(0).setCellValue(cne)
                        dbRef.getReference("students").get().addOnSuccessListener {
                            for (ds in it.children) {
                                if (ds.child("cne").value.toString().equals(cne)) {
                                    row.createCell(1).setCellValue(ds.child("last_name").value.toString())
                                    row.createCell(2).setCellValue(ds.child("first_name").value.toString())
                                }
                            }

                            //ask for permission to write to external storage
                            Dexter.withActivity(this)
                                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .withListener(object : PermissionListener {
                                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                        // permission was granted, you can perform your action
                                        //get root directory
                                        val dir = File(
                                            Environment.getExternalStorageDirectory()
                                                .toString() + "/AbsenceManagementApp"
                                        )
                                        if (!dir.exists()) {
                                            dir.mkdir()
                                        }
                                        val file_name = "absences-${Date().time}.xlsx"
                                        val file = File(dir, file_name)
                                        try {
                                            file.createNewFile()
                                            val outputStream = file.outputStream()
                                            workbook.write(outputStream)
                                            outputStream.close()

                                            FancyToast.makeText(
                                                this@ModuleActivity,
                                                "File exported successfully to ${dir.absolutePath}",
                                                FancyToast.LENGTH_LONG,
                                                FancyToast.SUCCESS,
                                                false
                                            ).show()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                        // permission was denied, you can show a message to the user
                                        Toast.makeText(
                                            this@ModuleActivity,
                                            "Permission denied",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    override fun onPermissionRationaleShouldBeShown(
                                        permission: PermissionRequest?,
                                        token: PermissionToken?
                                    ) {
                                        // permission was not granted, you can request that the user grant the permission
                                        token?.continuePermissionRequest()
                                    }
                                }).check()
                        }
                    }
                }
            }
        }
    }

    private fun createExcelFile() {
        val file_path = getExternalFilesDir(null)?.absolutePath
        val file_name = "absences.xls"

        Log.e("debug", "file_path: $file_path/$file_name")

        try {
            val outputStream = FileOutputStream("$file_path/$file_name")
            val workbook = HSSFWorkbook()

            // export to excel
            var rowNum = 0
            var cellNum = 3

            Log.d("debug", "Export to excel")
            workbook.createSheet("Absences")
            val sheet = workbook.getSheetAt(0)
            val row = sheet.createRow(++rowNum)

            row.createCell(1).setCellValue("CNE")
            row.createCell(2).setCellValue("Nom")
            row.createCell(3).setCellValue("Prénom")

            dbRef.getReference("inscription").get().addOnSuccessListener {
                for (ds in it.children) {
                    if (ds.child("n_module").value.toString().equals(currentModuleId)) {
                        val row = sheet.createRow(++rowNum)
                        dbRef.getReference("students").get().addOnSuccessListener {
                            for (ds1 in it.children) {
                                if (ds1.child("cne").value.toString()
                                        .equals(ds.child("cne").value.toString())
                                ) {
                                    row.createCell(1)
                                        .setCellValue(ds1.child("cne").value.toString())
                                    Log.e("debug", "cne: ${ds1.child("cne").value.toString()}")

                                    row.createCell(2)
                                        .setCellValue(ds1.child("first_name").value.toString())
                                    Log.e(
                                        "debug",
                                        "first_name: ${ds1.child("first_name").value.toString()}"
                                    )

                                    row.createCell(3)
                                        .setCellValue(ds1.child("last_name").value.toString())
                                    Log.e(
                                        "debug",
                                        "last_name: ${ds1.child("last_name").value.toString()}"
                                    )
                                }
                            }
                        }
                    }
                }

                dbRef.getReference("seances").get().addOnSuccessListener {
                    rowNum = 1

                    for (ds2 in it.children) {
                        if (ds2.child("n_module").value.toString().equals(currentModuleId)) {
                            row.createCell(++cellNum).setCellValue(
                                ds2.child("date").value.toString() + " " + ds2.child("start_time").value.toString() + " - " + ds2.child(
                                    "end_time"
                                ).value.toString() + " (" + ds2.child("type").value.toString() + ")"
                            )

                            dbRef.getReference("absences").get().addOnSuccessListener {
                                for (ds3 in it.children) {
                                    if (ds3.child("seance_id").value.toString()
                                            .equals(ds2.child("id").value.toString()) && ds3.child(
                                            "cne"
                                        ).value.toString()
                                            .equals(row.getCell(1).toString())
                                    ) {
                                        when (ds3.child("_present").value.toString()) {
                                            "true" -> {
                                                row.createCell(cellNum).setCellValue("P")
                                                Log.e("debug", "Present")
                                            }
                                            "false" -> {
                                                row.createCell(cellNum).setCellValue("A")
                                                Log.e("debug", "Absent")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    workbook.write(outputStream)
                    workbook.close()
                    outputStream.close()

                    Toast.makeText(this, "Excel file saved", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Log.e("debug", "Error getting inscription data", it)
            }
        } catch (e: Exception) {
            Log.e("debug", "Error creating excel file", e)
        }
    }

    companion object {
        private val REQUEST_CODE_CHOOSE_DIRECTORY = 1
    }
}

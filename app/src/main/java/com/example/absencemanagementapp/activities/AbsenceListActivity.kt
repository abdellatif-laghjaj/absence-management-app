package com.example.absencemanagementapp.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.adapters.AbsenceAdapter
import com.example.absencemanagementapp.models.Absence
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
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
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.text.DateFormat
import java.util.Date


class AbsenceListActivity : AppCompatActivity() {
    private lateinit var absence_list_rv: RecyclerView
    private lateinit var back_iv: ImageView
    private lateinit var export_fab: ExtendedFloatingActionButton

    private lateinit var absence_adapter: AbsenceAdapter

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var seance_id: String
    private lateinit var absences: ArrayList<Absence>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absence_list)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        seance_id = intent.getStringExtra("seance_id").toString()

        //initiate views
        initViews()

        getAbsences()

        back_iv.setOnClickListener { back() }

        export_fab.setOnClickListener {
            exportAbsencesToExcel(absences)
        }
    }

    override fun onBackPressed() {
        back()
    }

    private fun getAbsences() {
        database.getReference("absences").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(it: DataSnapshot) {
                absences = ArrayList()
                for (ds in it.children) {
                    if (ds.child("seance_id").value.toString().equals(seance_id)) {
                        val id = ds.child("id").value.toString()
                        val cne = ds.child("cne").value.toString()
                        val is_present: Boolean? = ds.child("_present").value as Boolean?
                        var absence = Absence()
                        if (is_present != null) {
                            absence = Absence(id, cne, seance_id, is_present)
                        }
                        absences.add(absence)
                    }
                }
                displayAbsences(absences)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun displayAbsences(absences: ArrayList<Absence>) {
        //add a divider between items
        val dividerItemDecoration = DividerItemDecoration(
            absence_list_rv.context, LinearLayoutManager.VERTICAL
        )
        absence_list_rv.addItemDecoration(dividerItemDecoration)
        absence_adapter = AbsenceAdapter(absences, this, database)
        absence_list_rv.adapter = absence_adapter
        absence_list_rv.setHasFixedSize(true)
        absence_list_rv.layoutManager = LinearLayoutManager(this)
    }

    private fun back() {
        finish()
    }

    private fun initViews() {
        absence_list_rv = findViewById(R.id.absence_list_rv)
        back_iv = findViewById(R.id.back_arrow)
        export_fab = findViewById(R.id.export_fab)
    }

    //export absence list to excel file
    private fun exportAbsencesToExcel(data: ArrayList<Absence>) {
        val progress_dialog = ProgressDialog(this)
        progress_dialog.setTitle("Exporting...")
        progress_dialog.setMessage("Please wait while we are exporting your data to excel file")
        progress_dialog.show()

        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Absences")

        //get number of columns
        val columns =
            arrayOf("cne", "first name", "last name", "presence status", "date")

        //create header row
        val headerRow = sheet.createRow(0)
        for (i in columns.indices) {
            val cell = headerRow.createCell(i)
            cell.setCellValue(columns[i].uppercase())
        }

        //add padding to header row
        val headerCellStyle = workbook.createCellStyle()
        headerCellStyle.fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
        headerCellStyle.fillPattern = FillPatternType.SOLID_FOREGROUND
        for (i in columns.indices) {
            val cell = headerRow.getCell(i)
            cell.cellStyle = headerCellStyle
        }

        //set column width
        for (i in columns.indices) {
            sheet.setColumnWidth(i, 6000)
        }

        for (i in data.indices) {
            val df = DateFormat.getDateInstance(DateFormat.FULL)

            val (first_name, last_name) = getStudentName(data[i].cne)

            val row = sheet.createRow(i + 1)
            row.createCell(0).setCellValue(data[i].cne)
            row.createCell(1).setCellValue(first_name)
            row.createCell(2).setCellValue(last_name)
            row.createCell(3).setCellValue(if (data[i].is_present) "present" else "absent")
            row.createCell(4).setCellValue(df.format(Date()))
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
                            this@AbsenceListActivity,
                            "File exported successfully to ${dir.absolutePath}",
                            FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS,
                            false
                        ).show()

                        progress_dialog.dismiss()
                    } catch (e: Exception) {
                        progress_dialog.dismiss()
                        e.printStackTrace()
                    }
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    // permission was denied, you can show a message to the user
                    Toast.makeText(
                        this@AbsenceListActivity,
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

    //get student from cne
    private fun getStudentName(cne: String): Pair<String, String> {
        var first_name = ""
        var last_name = ""
        database.getReference("students").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(it: DataSnapshot) {
                for (ds in it.children) {
                    if (ds.child("cne").value.toString() == cne) {
                        first_name = ds.child("first_name").value.toString()
                        last_name = ds.child("last_name").value.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        return Pair(first_name, last_name)
    }
}

package com.example.absencemanagementapp.activities

import android.Manifest
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import pub.devrel.easypermissions.EasyPermissions
import java.io.File


class AbsenceListActivity : AppCompatActivity() {
    private val REQUEST_CODE: Int = 100
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
        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Absences")

        //get number of columns
        val columns = arrayOf("cne", "first name", "last name", "seance_id", "presence status")

        //create header row
        val headerRow = sheet.createRow(0)
        for (i in columns.indices) {
            val cell = headerRow.createCell(i)
            cell.setCellValue(columns[i])
        }

        for (i in data.indices) {
            val row = sheet.createRow(i)
            row.createCell(0).setCellValue(data[i].cne)
            row.createCell(1).setCellValue("first name")
            row.createCell(2).setCellValue("last name")
            row.createCell(3).setCellValue(data[i].seance_id)
            row.createCell(4).setCellValue(data[i].is_present)
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
                    val file = File(dir, "absences.xlsx")
                    try {
                        file.createNewFile()
                        val outputStream = file.outputStream()
                        workbook.write(outputStream)
                        outputStream.close()
                        Toast.makeText(
                            this@AbsenceListActivity,
                            "File exported successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
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
}

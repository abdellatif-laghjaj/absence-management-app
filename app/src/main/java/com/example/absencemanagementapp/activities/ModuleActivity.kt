package com.example.absencemanagementapp.activities

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.example.absencemanagementapp.models.Module
import com.example.absencemanagementapp.models.Seance
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.FirebaseDatabase
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Integer.parseInt

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

        absence_list_cv.setOnClickListener { exportExcel() }

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

    private fun exportExcel() {
        val fileChooser = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "application/vnd.ms-excel"
        }
        startActivityForResult(
            Intent.createChooser(fileChooser, R.string.select_directory.toString()),
            REQUEST_CODE_CHOOSE_DIRECTORY
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE_DIRECTORY && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                createExcelFile(this, uri)
                Log.e("debug", "Selected file: $uri")
            }
        }
    }

    private fun createExcelFile(activity: Activity, uri: Uri) {
        val contentResolver = activity.contentResolver
        val documentFile = DocumentFile.fromTreeUri(activity, uri)
        val fileName = "new_excel_file.xls"
        val excelFile = documentFile?.createFile("application/vnd.ms-excel", fileName)
        if (excelFile != null) {
            val outputStream = contentResolver.openOutputStream(excelFile.uri)
            if (outputStream != null) {
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
                                        row.createCell(2)
                                            .setCellValue(ds1.child("first_name").value.toString())
                                        row.createCell(3)
                                            .setCellValue(ds1.child("last_name").value.toString())
                                    }
                                }
                            }
                        }
                    }

                    dbRef.getReference("seances").get().addOnSuccessListener {
                        rowNum = 0

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
                                                "true" -> row.createCell(cellNum).setCellValue("P")
                                                "false" -> row.createCell(cellNum).setCellValue("A")
                                            }
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

                Toast.makeText(activity, "Excel file saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Unable to create file", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity, "Unable to create file", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private val REQUEST_CODE_CHOOSE_DIRECTORY = 1
    }
}

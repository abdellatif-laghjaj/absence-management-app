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
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.adapters.AbsenceAdapter
import com.example.absencemanagementapp.models.Absence
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
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
import org.apache.logging.log4j.ThreadContext.trim
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
    private lateinit var search_student_et: SearchView

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

        search_student_et.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
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
                        val filteredList = absences.filter { it.cne.contains(newText?.trim().toString(), ignoreCase = true) }
                        // Update the RecyclerView with the filtered list
                        absences = filteredList as ArrayList<Absence>
                        displayAbsences(absences)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException())
                    }
                })
                return true
            }

        })
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
        search_student_et = findViewById(R.id.search_student_et)
    }

}

package com.example.absencemanagementapp.activities

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.adapters.AbsenceAdapter
import com.example.absencemanagementapp.models.Absence
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AbsenceListActivity : AppCompatActivity() {
    private lateinit var absence_list_rv: RecyclerView
    private lateinit var back_iv: ImageView

    private lateinit var absence_adapter: AbsenceAdapter

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private lateinit var seance_id: String

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
    }

    override fun onBackPressed() {
        back()
    }

    private fun getAbsences() {
        database.getReference("absences").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(it: DataSnapshot) {
                val absences = ArrayList<Absence>()
                for (ds in it.children) {
                    if (ds.child("seance_id").value.toString().equals(seance_id)) {
                        val cne = ds.child("cne").value.toString()
                        val is_present: Boolean? = ds.child("_present").value as Boolean?
                        var absence = Absence()
                        if (is_present != null) {
                            absence = Absence(cne, seance_id, is_present)
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
        absence_adapter = AbsenceAdapter(absences, this)
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
    }
}

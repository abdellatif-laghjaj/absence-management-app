package com.example.absencemanagementapp.activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.adapters.AbsenceAdapter
import com.example.absencemanagementapp.models.Absence
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class AbsenceListActivity : AppCompatActivity() {
    private lateinit var absence_list_rv: RecyclerView
    private lateinit var back_iv: ImageView

    private lateinit var absence_adapter: AbsenceAdapter

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absence_list)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //initiate views
        initViews()

        back_iv.setOnClickListener { back() }

        //initiate absence adapter
        val absence_list = mutableListOf<Absence>()
        absence_list.add(Absence("cne 1", "188q7872", true))
        absence_list.add(Absence("cne 2", "188q7872", true))
        absence_list.add(Absence("cne 3", "188q7872", false))
        absence_list.add(Absence("cne 4", "188q7872", true))
        absence_list.add(Absence("cne 5", "188q7872", true))
        absence_list.add(Absence("cne 6", "188q7872", false))
        absence_list.add(Absence("cne 7", "188q7872", false))
        absence_list.add(Absence("cne 8", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))
        absence_list.add(Absence("cne 9", "188q7872", true))

        //add a divider between items
        val dividerItemDecoration = DividerItemDecoration(
            absence_list_rv.context, LinearLayoutManager.VERTICAL
        )
        absence_list_rv.addItemDecoration(dividerItemDecoration)
        absence_adapter = AbsenceAdapter(absence_list, this)
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
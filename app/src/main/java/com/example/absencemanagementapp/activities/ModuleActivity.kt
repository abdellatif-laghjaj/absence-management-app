package com.example.absencemanagementapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
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
//        export to excel
        Log.d("debug", "Export to excel")
    }
}

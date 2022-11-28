package com.example.absencemanagementapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.activities.home.TeacherActivity
import com.example.absencemanagementapp.adapters.SeanceAdapter
import com.example.absencemanagementapp.models.Module
import com.example.absencemanagementapp.models.Seance
import com.google.android.material.card.MaterialCardView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Integer.parseInt

class ModuleActivity : AppCompatActivity() {
    private lateinit var module_name_tv: TextView
    private lateinit var back_iv: ImageView
    private lateinit var seances_swipe: SwipeRefreshLayout
    private lateinit var rv: RecyclerView
    private lateinit var absence_list_cv: MaterialCardView
    private lateinit var new_seance_cv: MaterialCardView

    private lateinit var dbRef: FirebaseDatabase

    var currentModuleId: Int = -1
    var currentModuleIntitule: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module)

        dbRef = FirebaseDatabase.getInstance()

        initView()

        getSeances()

        currentModuleId = intent.getIntExtra("id", -1)
        setCurrentModuleIntitule(currentModuleId)

    }

    private fun initSeances(seances: ArrayList<Seance>) {
        currentModuleIntitule = intent.getStringExtra("module_intitule")
        currentModuleIntitule = module_name_tv.text as String?
        currentModuleId = intent.getIntExtra("id", -1)
        println("id before intent ===> " + currentModuleId)
        setCurrentModuleIntitule(currentModuleId)
        println("Before intent ===> " + currentModuleIntitule)
        rv = findViewById<RecyclerView>(R.id.seances_rv)
        rv.layoutManager = LinearLayoutManager(this)
        val seanceAdapter = SeanceAdapter(seances, this, currentModuleIntitule)
        rv.adapter = seanceAdapter
    }

    private fun initView() {
        currentModuleIntitule = intent.getStringExtra("module_intitule")
        module_name_tv = this.findViewById(R.id.module_intitule_tv)
        module_name_tv.text = currentModuleIntitule

        back_iv.setOnClickListener { back() }

        absence_list_cv.setOnClickListener { toAbsenceListView() }

        new_seance_cv.setOnClickListener { toNewSeanceView() }

        seances_swipe.setOnRefreshListener {
            getSeances()
            seances_swipe.isRefreshing = false
        }
    }

    private fun setCurrentModuleIntitule(id: Int) {

        dbRef.getReference("modules").child(id.toString()).child("intitule").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    module_name_tv.text = it.value.toString()
                }
            }
    }

    private fun getSeances() {
        dbRef.getReference("seances").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val seances = ArrayList<Seance>()
                for (ds in snapshot.children) {
                    val id = ds.key.toString()
                    val date = ds.child("date").value.toString()
                    val start_time = ds.child("start_time").value.toString()
                    val end_time = ds.child("end_time").value.toString()
                    val type = ds.child("type").value.toString()
                    val n_salle = ds.child("n_salle").value.toString()
                    val n_module = parseInt(ds.child("n_module").value.toString())
                    val total_absences = parseInt(ds.child("total_absences").value.toString())
                    var seance = Seance(
                        id, date, start_time, end_time, type, n_salle, n_module, total_absences
                    )
                    seances.add(seance)
                }
                initSeances(seances)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
//        val seances = ArrayList<Seance>()
//        seances.add(Seance("16/11/2022", "TP", 4))
//        seances.add(Seance("15/11/2022", "Cour", 12))
//        seances.add(Seance("08/11/2022", "Cour", 9))
//        seances.add(Seance("01/11/2022", "Cour", 4))
//        seances.add(Seance("24/10/2022", "TP", 4))
//        seances.add(Seance("17/10/2022", "Cour", 4))
//        return seances
    }

    private fun back() {
        val intent = Intent(this, TeacherActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toNewSeanceView() {
        val intent = Intent(this, NewSeanceActivity::class.java)
        intent.putExtra("id", currentModuleId)
        startActivity(intent)
        finish()
    }

    private fun toAbsenceListView() {
        val intent = Intent(this, TeacherActivity::class.java)
        startActivity(intent)
        finish()
    }
}
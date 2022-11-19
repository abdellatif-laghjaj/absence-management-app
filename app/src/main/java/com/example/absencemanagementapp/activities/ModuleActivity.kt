package com.example.absencemanagementapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.adapters.SeanceAdapter
import com.example.absencemanagementapp.models.Module
import com.example.absencemanagementapp.models.Seance
import com.google.android.material.card.MaterialCardView

class ModuleActivity : AppCompatActivity() {
    private lateinit var module_name_tv: TextView
    private lateinit var nb_students_tv: TextView
    private lateinit var back_iv: ImageView
    private lateinit var absence_list_cv: MaterialCardView
    private lateinit var new_seance_cv: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module)

        //
        initView()
        //

        // region: Display all seances of module
        val rv = findViewById<RecyclerView>(R.id.seances_rv)
        rv.layoutManager = LinearLayoutManager(this)
        val seances = getSeances()
        val seanceAdapter =
            SeanceAdapter(seances, this, getCurrentModule(intent.getIntExtra("id", 0)).inititule)
        rv.adapter = seanceAdapter
        // endregion
    }

    private fun initView() {
        var id = intent.getIntExtra("id", 0)
        var modules = getCurrentModule(id)
        module_name_tv = findViewById(R.id.module_intitule_tv)
        module_name_tv.setText(modules.inititule)

        back_iv = findViewById(R.id.back_arrow)
        back_iv.setOnClickListener({ back() })

        nb_students_tv = findViewById(R.id.nb_students_tv)
        nb_students_tv.setText("77")

        absence_list_cv = findViewById(R.id.absence_list_cv)
        absence_list_cv.setOnClickListener({ toAbsenceListView() })

        new_seance_cv = findViewById(R.id.new_seance_cv)
        new_seance_cv.setOnClickListener({ toNewSeanceView() })
    }

    private fun getCurrentModule(id: Int): Module {
        val modules = ArrayList<Module>();

        modules.add(Module(1, "Algebre 1", "ALG1", 1, 9))
        modules.add(Module(2, "Analyse 1", "ALG1", 1, 9))
        modules.add(Module(3, "Physique 1", "ALG1", 1, 9))
        modules.add(Module(4, "Probabilité statistique", "ALG1", 1, 9))
        modules.add(Module(5, "Algorithmique et programmation 1", "ALG1", 1, 9))
        modules.add(Module(6, "Langues et terminologie 1", "ALG1", 1, 9))
        modules.add(Module(7, "Environnement d'entreprise", "ALG1", 1, 9))

        return modules[id]
    }

    private fun getSeances(): List<Seance> {
        val seances = ArrayList<Seance>()
        seances.add(Seance("16/11/2022", "TP", 4))
        seances.add(Seance("15/11/2022", "Cour", 12))
        seances.add(Seance("08/11/2022", "Cour", 9))
        seances.add(Seance("01/11/2022", "Cour", 4))
        seances.add(Seance("24/10/2022", "TP", 4))
        seances.add(Seance("17/10/2022", "Cour", 4))
        return seances
    }

    private fun back() {
        val intent = Intent(this, TeacherActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toNewSeanceView() {
        val intent = Intent(this, TeacherActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun toAbsenceListView() {
        val intent = Intent(this, TeacherActivity::class.java)
        startActivity(intent)
        finish()
    }
}
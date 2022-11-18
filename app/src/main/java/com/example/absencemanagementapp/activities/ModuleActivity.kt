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

class ModuleActivity : AppCompatActivity() {
    private lateinit var module_name_tv: TextView
    private lateinit var back_iv: ImageView
    var id = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module)

        //
        val modules = ArrayList<Module>();
        modules.add(Module(1, "Algebre 1", "ALG1", 1, 9))
        modules.add(Module(2, "Analyse 1", "ALG1", 1, 9))
        modules.add(Module(3, "Physique 1", "ALG1", 1, 9))
        modules.add(Module(4, "Probabilité statistique", "ALG1", 1, 9))
        modules.add(Module(5, "Algorithmique et programmation 1", "ALG1", 1, 9))
        modules.add(Module(6, "Langues et terminologie 1", "ALG1", 1, 9))
        modules.add(Module(7, "Environnement d'entreprise", "ALG1", 1, 9))
        //

        //
        module_name_tv = findViewById(R.id.module_intitule_tv)
        module_name_tv.setText(modules[id].inititule)
        back_iv = findViewById(R.id.back_iv)
        back_iv.setOnClickListener({ back() })
        //

        // region: Display all seances of module
        val rv = findViewById<RecyclerView>(R.id.seances_rv)
        rv.layoutManager = LinearLayoutManager(this)
        val seances = ArrayList<Seance>()
        seances.add(Seance("16/11/2022", "TP", 4))
        seances.add(Seance("15/11/2022", "Cour", 12))
        seances.add(Seance("08/11/2022", "Cour", 9))
        seances.add(Seance("01/11/2022", "Cour", 4))
        seances.add(Seance("24/10/2022", "TP", 4))
        seances.add(Seance("17/10/2022", "Cour", 4))
        val seanceAdapter = SeanceAdapter(seances, this)
        rv.adapter = seanceAdapter
        // endregion
    }

    private fun back() {
        val intent = Intent(this, TeacherActivity::class.java)
        startActivity(intent)
        finish()
    }
}
package com.example.absencemanagementapp.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.models.Seance

class SeanceActivity : AppCompatActivity() {
    lateinit var module_intitule_tv: TextView
    lateinit var seance_type_tv: TextView
    lateinit var seance_date_tv: TextView
    lateinit var seance_start_time_tv: TextView
    lateinit var seance_end_time_tv: TextView
    lateinit var salle_nb_tv: TextView
    lateinit var total_absence_tv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seance)

        initView()
    }

    private fun initView() {
        var seance_id = intent.getIntExtra("id", 0)
        var module_intitule = intent.getStringExtra("module_intitule")

        module_intitule_tv = this.findViewById(R.id.module_intitule_tv)
        module_intitule_tv.setText(module_intitule)

        seance_type_tv = this.findViewById(R.id.seance_type_tv)
        seance_type_tv.setText(getCurrentSeance(seance_id).type)

        seance_date_tv = this.findViewById(R.id.seance_date_tv)
        seance_date_tv.setText(getCurrentSeance(seance_id).date)

        seance_start_time_tv = this.findViewById(R.id.seance_start_time_tv)
        seance_type_tv.setText(getCurrentSeance(seance_id).start_time)

        seance_end_time_tv = this.findViewById(R.id.seance_end_time_tv)
        seance_type_tv.setText(getCurrentSeance(seance_id).end_time)

        salle_nb_tv = this.findViewById(R.id.salle_nb_tv)
        seance_type_tv.setText(getCurrentSeance(seance_id).n_salle)

        total_absence_tv = this.findViewById(R.id.total_absence_tv)
        seance_type_tv.setText(getCurrentSeance(seance_id).total_absences)
    }

    private fun getCurrentSeance(id: Int): Seance {
        val seances = ArrayList<Seance>()
        seances.add(Seance("16/11/2022", "TP", 4))
        seances.add(Seance("15/11/2022", "Cour", 12))
        seances.add(Seance("08/11/2022", "Cour", 9))
        seances.add(Seance("01/11/2022", "Cour", 4))
        seances.add(Seance("24/10/2022", "TP", 4))
        seances.add(Seance("17/10/2022", "Cour", 4))
        return seances[id]
    }
}
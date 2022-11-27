package com.example.absencemanagementapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.models.Seance
import com.google.android.material.card.MaterialCardView

class SeanceActivity : AppCompatActivity() {
    lateinit var module_intitule_tv: TextView
    lateinit var back_iv: ImageView

    lateinit var absence_list_cv: MaterialCardView
    lateinit var show_qr_code: MaterialCardView

    lateinit var seance_type_tv: TextView
    lateinit var seance_date_tv: TextView
    lateinit var seance_start_time_tv: TextView
    lateinit var seance_end_time_tv: TextView
    lateinit var salle_nb_tv: TextView
    lateinit var total_absence_tv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seance)

        //initiate views
        initView()

        //get seance from intent
        setDatas()

        back_iv.setOnClickListener({ back() })

        absence_list_cv.setOnClickListener({ showAbsenceList() })

        show_qr_code.setOnClickListener({ showQrCode() })
    }

    private fun initView() {
        module_intitule_tv = this.findViewById(R.id.module_intitule_tv)
        back_iv = findViewById(R.id.back_arrow)
        absence_list_cv = this.findViewById(R.id.absence_list_cv)
        show_qr_code = this.findViewById(R.id.show_qr_code)
        seance_type_tv = this.findViewById(R.id.seance_type_tv)
        seance_date_tv = this.findViewById(R.id.seance_date_tv)
        seance_start_time_tv = this.findViewById(R.id.seance_start_time_tv)
        seance_end_time_tv = this.findViewById(R.id.seance_end_time_tv)
        salle_nb_tv = this.findViewById(R.id.salle_nb_tv)
        total_absence_tv = this.findViewById(R.id.total_absence_tv)
    }

    private fun getCurrentSeance(id: Int): Seance {
        val seances = ArrayList<Seance>()
        seances.add(Seance("16/11/2022", "TP", "08:30", "10:15", "Salle 1", 0))
        seances.add(Seance("16/11/2022", "Cours", "10:30", "12:15", "Salle 2", 0))
        seances.add(Seance("16/11/2022", "Exam", "12:30", "14:15", "Salle 3", 0))
        seances.add(Seance("16/11/2022", "Cours", "14:30", "16:15", "Salle 4", 0))
        seances.add(Seance("16/11/2022", "TP", "16:30", "18:15", "Salle 5", 0))
        return seances[id]
    }

    private fun back() {
        val intent = Intent(this, ModuleActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showQrCode() {
        println("showQrCode")
    }

    private fun showAbsenceList() {
        println("showAbsenceList")
    }

    private fun setDatas() {
        val seance_id = intent.getIntExtra("id", 0)
        val module_intitule = intent.getStringExtra("module_intitule")

        module_intitule_tv.text = module_intitule
        seance_type_tv.text = getCurrentSeance(seance_id).type
        seance_date_tv.text = getCurrentSeance(seance_id).date
        seance_start_time_tv.text = getCurrentSeance(seance_id).start_time
        seance_end_time_tv.text = getCurrentSeance(seance_id).end_time
        salle_nb_tv.text = getCurrentSeance(seance_id).n_salle
        total_absence_tv.text = getCurrentSeance(seance_id).total_absences.toString()
    }
}
package com.example.absencemanagementapp.activities

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.example.absencemanagementapp.R
import java.util.*

class NewSeanceActivity : AppCompatActivity() {
    private lateinit var back_iv: ImageView

    lateinit var type_dropdown: AutoCompleteTextView
    lateinit var seance_date: AutoCompleteTextView
    lateinit var start_dropdown: AutoCompleteTextView
    lateinit var end_dropdown: AutoCompleteTextView
    lateinit var salle_dropdown: AutoCompleteTextView

    lateinit var update_btn: AppCompatButton

    var types = arrayOf("Cours", "TP", "Exam")
    var startHours = arrayOf("08:30", "10:30", "12:30", "14:30", "16:30")
    var endHours = arrayOf("10:15", "12:15", "14:15", "16:15", "18:15")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_seance)

        initView()
    }

    private fun initView() {
        back_iv = this.findViewById(R.id.back_iv)
        back_iv.setOnClickListener({ back() })

        initFields()
    }

    private fun initFields() {
        type_dropdown = this.findViewById(R.id.type_dropdown)
        type_dropdown.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, types))

        seance_date = this.findViewById(R.id.seance_date)
        seance_date.setOnClickListener {
            val c = Calendar.getInstance()

            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                this,
                { view, year, monthOfYear, dayOfMonth ->
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        start_dropdown = this.findViewById(R.id.start_dropdown)
        start_dropdown.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, startHours))

        end_dropdown = this.findViewById(R.id.end_dropdown)
        end_dropdown.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, endHours))

        salle_dropdown = this.findViewById(R.id.salle_dropdown)
        salle_dropdown.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, getLocales()))

        update_btn = this.findViewById(R.id.update_btn)
        update_btn.setOnClickListener({ addNewSeance() })
    }

    private fun addNewSeance() {
        println("ADD NEW SEANCE")
    }

    private fun getLocales(): Array<String> {
        return arrayOf(
            "A1",
            "A2",
            "A3",
            "A4",
            "SL1",
            "SL2",
            "TD1",
            "TD2",
            "TD3",
            "M1",
            "M2",
            "M3",
            "Info1",
            "Info2",
            "Info3"
        )
    }

    private fun back() {
        intent = Intent(this, ModuleActivity::class.java)
        intent.putExtra("id", intent.getIntExtra("id", 0))
        finish()
    }
}
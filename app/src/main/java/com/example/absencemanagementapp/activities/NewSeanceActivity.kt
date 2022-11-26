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
import com.example.absencemanagementapp.models.Seance
import java.util.*

class NewSeanceActivity : AppCompatActivity() {
    private lateinit var back_iv: ImageView

    private lateinit var type_dropdown: AutoCompleteTextView
    private lateinit var seance_date: AutoCompleteTextView
    private lateinit var start_dropdown: AutoCompleteTextView
    private lateinit var end_dropdown: AutoCompleteTextView
    private lateinit var salle_dropdown: AutoCompleteTextView

    lateinit var add_seance_btn: AppCompatButton

    var id = 0

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

        id = intent.getIntExtra("id", 0)
        initFields()
    }

    private fun initFields() {
        type_dropdown = this.findViewById(R.id.type_dropdown)
        type_dropdown.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, types))

        seance_date = this.findViewById(R.id.seance_date)
        seance_date.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val date_picker_dialog = DatePickerDialog(
                this, { view, selected_year, selected_month, selected_day ->
                    seance_date.setText("$selected_day/${selected_month + 1}/$selected_year")
                },
                year,
                month,
                day
            )
            date_picker_dialog.show()
        }

        start_dropdown = this.findViewById(R.id.start_dropdown)
        start_dropdown.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, startHours))

        end_dropdown = this.findViewById(R.id.end_dropdown)
        end_dropdown.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, endHours))

        salle_dropdown = this.findViewById(R.id.salle_dropdown)
        salle_dropdown.setAdapter(ArrayAdapter(this, R.layout.dropdown_item, getLocales()))

        add_seance_btn = this.findViewById(R.id.add_seance_btn)
        add_seance_btn.setOnClickListener({
            if (validateInputs()) {
                addNewSeance()
            }
        })
    }

    private fun addNewSeance() {
        //create new seance
        val seance = Seance()

        seance.type = type_dropdown.text.toString()
        seance.date = seance_date.text.toString()
        seance.start_time = start_dropdown.text.toString()
        seance.end_time = end_dropdown.text.toString()
        seance.n_salle = salle_dropdown.text.toString()
        seance.n_module = id

        println(seance.toString())
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
        intent.putExtra("id", id)
        startActivity(intent)
        finish()
    }

    private fun validateInputs(): Boolean {
        return when {
            type_dropdown.text.toString().isEmpty() -> {
                type_dropdown.error = "Type is required"
                false
            }
            seance_date.text.toString().isEmpty() -> {
                seance_date.error = "Date is required"
                false
            }
            start_dropdown.text.toString().isEmpty() -> {
                start_dropdown.error = "Start time is required"
                false
            }
            end_dropdown.text.toString().isEmpty() -> {
                end_dropdown.error = "End time is required"
                false
            }
            salle_dropdown.text.toString().isEmpty() -> {
                salle_dropdown.error = "Salle is required"
                false
            }
            else -> true
        }
    }
}
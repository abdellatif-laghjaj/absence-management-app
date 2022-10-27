package com.example.absencemanagementapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : AppCompatActivity() {
    private lateinit var login_tv: TextView
    private lateinit var first_name_et: TextInputEditText
    private lateinit var last_name_et: TextInputEditText
    private lateinit var cin_et: TextInputEditText
    private lateinit var cne_et: TextInputEditText
    private lateinit var filiere_dropdown: AutoCompleteTextView
    private lateinit var semester_dropdown: AutoCompleteTextView
    private lateinit var email_et: TextInputEditText
    private lateinit var password_et: TextInputEditText
    private lateinit var confirm_password_et: TextInputEditText
    private lateinit var user_role_rg: RadioGroup
    private lateinit var student_rb: RadioButton
    private lateinit var teacher_rb: RadioButton
    private lateinit var register_btn: Button

    private val semsters = arrayOf("1", "2", "3", "4", "5", "6")
    private val branches = arrayOf("GI", "SV", "LAE", "ECO")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //initiate views and adapters for dropdowns
        initViews()
        initDropDowns()

        filiere_dropdown.setOnItemClickListener { adapterView, _, i, _ ->
            Toast.makeText(
                this, "Selected branch: ${adapterView.getItemAtPosition(i)}", Toast.LENGTH_SHORT
            ).show()
        }

        semester_dropdown.setOnItemClickListener { adapterView, _, i, _ ->
            Toast.makeText(
                this, "Selected semester: ${adapterView.getItemAtPosition(i)}", Toast.LENGTH_SHORT
            ).show()
        }

        login_tv.setOnClickListener {
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        register_btn.setOnClickListener {
            validateFields()
        }
    }

    private fun validateFields(): Boolean {
        if (first_name_et.text.toString().isEmpty()) {
            first_name_et.setError("First name is required")
            return false
        } else {
            first_name_et.setError(null)
        }
        if (last_name_et.text.toString().isEmpty()) {
            last_name_et.setError("Last name is required")
            return false
        } else {
            last_name_et.setError(null)
        }
        if (cin_et.text.toString().isEmpty()) {
            cin_et.setError("CIN is required")
            return false
        } else {
            cin_et.setError(null)
        }
        if (cne_et.text.toString().isEmpty()) {
            cne_et.setError("CNE is required")
            return false
        } else {
            cne_et.setError(null)
        }
        if (filiere_dropdown.text.toString().isEmpty()) {
            filiere_dropdown.setError("Filiere is required")
            return false
        } else {
            filiere_dropdown.setError(null)
        }
        if (semester_dropdown.text.toString().isEmpty()) {
            semester_dropdown.setError("Semester is required")
            return false
        } else {
            semester_dropdown.setError(null)
        }
        if (email_et.text.toString().isEmpty()) {
            email_et.setError("Email is required")
            return false
        } else {
            email_et.setError(null)
        }
        if (password_et.text.toString().isEmpty()) {
            password_et.setError("Password is required")
            return false
        } else {
            password_et.setError(null)
        }
        if (confirm_password_et.text.toString().isEmpty()) {
            confirm_password_et.setError("Confirm password is required")
            return false
        } else {
            confirm_password_et.setError(null)
        }
        if (password_et.text.toString() != confirm_password_et.text.toString()) {
            confirm_password_et.setError("Passwords do not match")
            return false
        } else {
            confirm_password_et.setError(null)
        }

        return true
    }

    private fun resetFields() {
        first_name_et.setText("")
        last_name_et.setText("")
        cin_et.setText("")
        cne_et.setText("")
        filiere_dropdown.setText("")
        semester_dropdown.setText("")
        email_et.setText("")
        password_et.setText("")
        confirm_password_et.setText("")
    }

    private fun initViews() {
        login_tv = findViewById(R.id.login_tv)
        first_name_et = findViewById(R.id.first_name_et)
        last_name_et = findViewById(R.id.last_name_et)
        cin_et = findViewById(R.id.cin_et)
        cne_et = findViewById(R.id.cne_et)
        filiere_dropdown = findViewById(R.id.filiere_dropdown)
        semester_dropdown = findViewById(R.id.semester_dropdown)
        email_et = findViewById(R.id.email_et)
        password_et = findViewById(R.id.password_et)
        confirm_password_et = findViewById(R.id.confirm_password_et)
        user_role_rg = findViewById(R.id.user_role_rg)
        register_btn = findViewById(R.id.register_btn)
    }

    private fun initDropDowns() {
        val adapter = ArrayAdapter(this, R.layout.dropdown_item, branches)
        filiere_dropdown.setAdapter(adapter)
        val adapter2 = ArrayAdapter(this, R.layout.dropdown_item, semsters)
        semester_dropdown.setAdapter(adapter2)
    }
}
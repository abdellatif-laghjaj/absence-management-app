package com.example.absencemanagementapp.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.absencemanagementapp.LoginActivity
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.models.Student
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class StudentRegisterFragment : Fragment() {
    private lateinit var first_name_et: TextInputEditText
    private lateinit var last_name_et: TextInputEditText
    private lateinit var cin_et: TextInputEditText
    private lateinit var cne_et: TextInputEditText
    private lateinit var filiere_dropdown: AutoCompleteTextView
    private lateinit var semester_dropdown: AutoCompleteTextView
    private lateinit var email_et: TextInputEditText
    private lateinit var password_et: TextInputEditText
    private lateinit var confirm_password_et: TextInputEditText
    private lateinit var register_btn: Button

    private val semsters = arrayOf("1", "2", "3", "4", "5", "6")
    private val branches = arrayOf("GI", "SV", "LAE", "ECO")

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //put the code here
        initViews()
        initDropDowns()
        filiere_dropdown.setOnItemClickListener { adapterView, _, i, _ ->
            //adapterView.getItemAtPosition(i)
        }

        semester_dropdown.setOnItemClickListener { adapterView, _, i, _ ->
            //adapterView.getItemAtPosition(i)
        }

        //regsitration logic
        register_btn.setOnClickListener {
            val first_name = first_name_et.text.toString()
            val last_name = last_name_et.text.toString()
            val cin = cin_et.text.toString()
            val cne = cne_et.text.toString()
            val filiere = filiere_dropdown.text.toString()
            val semester = semester_dropdown.text.toString()
            val email = email_et.text.toString()
            val password = password_et.text.toString()
            val confirm_password = confirm_password_et.text.toString()
            if (validateInputs()) {
                //register the student
                //dialog
                val dialog = AlertDialog.Builder(context)
                dialog.setTitle("Register")
                dialog.setMessage(
                    "Your information are: \n" + "First name: $first_name \n" + "Last name: $last_name \n" + "CIN: $cin \n" + "CNE: $cne \n" + "Filiere: $filiere \n" + "Semester: $semester \n" + "Email: $email \n" + "Password: $password \n" + "Confirm password: $confirm_password \n"
                )
                dialog.setPositiveButton("OK") { _, _ ->
                    //do nothing
                }
                dialog.show()

                //register student
                registerStudent()
            }
        }
    }

    private fun registerStudent() {
        //register student in firebase
        var student = Student(
            first_name_et.text.toString(),
            last_name_et.text.toString(),
            cin_et.text.toString(),
            cne_et.text.toString(),
            filiere_dropdown.text.toString(),
            semester_dropdown.text.toString(),
            email_et.text.toString(),
            password_et.text.toString()
        )
        database = FirebaseDatabase.getInstance()
        val ref = database.getReference("students")
        ref.child(student.cne).setValue(student)
        Toast.makeText(context, "Student registered successfully", Toast.LENGTH_SHORT).show()

        //redirect to login
        redirectToLogin()
    }

    private fun initViews() {
        first_name_et = view?.findViewById(R.id.first_name_et)!!
        last_name_et = view?.findViewById(R.id.last_name_et)!!
        cin_et = view?.findViewById(R.id.cin_et)!!
        cne_et = view?.findViewById(R.id.cne_et)!!
        filiere_dropdown = view?.findViewById(R.id.filiere_dropdown)!!
        semester_dropdown = view?.findViewById(R.id.semester_dropdown)!!
        email_et = view?.findViewById(R.id.email_et)!!
        password_et = view?.findViewById(R.id.password_et)!!
        confirm_password_et = view?.findViewById(R.id.confirm_password_et)!!
        register_btn = view?.findViewById(R.id.register_btn)!!
    }

    private fun initDropDowns() {
        val filiere_adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, branches)
        filiere_dropdown.setAdapter(filiere_adapter)
        val semester_adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, semsters)
        semester_dropdown.setAdapter(semester_adapter)
    }

    private fun validateInputs(): Boolean {
        val first_name = first_name_et.text.toString()
        val last_name = last_name_et.text.toString()
        val cin = cin_et.text.toString()
        val cne = cne_et.text.toString()
        val filiere = filiere_dropdown.text.toString()
        val semester = semester_dropdown.text.toString()
        val email = email_et.text.toString()
        val password = password_et.text.toString()
        val confirm_password = confirm_password_et.text.toString()
        return when {
            first_name.isEmpty() -> {
                first_name_et.error = "First name is required"
                false
            }
            last_name.isEmpty() -> {
                last_name_et.error = "Last name is required"
                false
            }
            cin.isEmpty() -> {
                cin_et.error = "CIN is required"
                false
            }
            cne.isEmpty() -> {
                cne_et.error = "CNE is required"
                false
            }
            filiere.isEmpty() -> {
                filiere_dropdown.error = "Filiere is required"
                false
            }
            semester.isEmpty() -> {
                semester_dropdown.error = "Semester is required"
                false
            }
            email.isEmpty() -> {
                email_et.error = "Email is required"
                false
            }
            password.isEmpty() -> {
                password_et.error = "Password is required"
                false
            }
            confirm_password.isEmpty() -> {
                confirm_password_et.error = "Confirm password is required"
                false
            }
            password != confirm_password -> {
                confirm_password_et.error = "Passwords don't match"
                false
            }
            else -> true
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }
}
package com.example.absencemanagementapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.absencemanagementapp.activities.auth.LoginActivity
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.models.Student
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.*

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
    private lateinit var login_tv: TextView

    private val semsters = arrayOf("1", "2", "3", "4", "5", "6")
    private val branches = arrayOf("GI", "SV", "LAE", "ECO")

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

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
            if (validateInputs()) {
                val email = email_et.text.toString().trim()
                val password = password_et.text.toString().trim()
                //register student
                registerStudent(email, password)
            }
        }

        login_tv.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerStudent(email: String, password: String) {
        //register student in firebase
        auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //register student in database
                val student = Student(
                    first_name_et.text.toString().trim().uppercase(Locale.getDefault()),
                    last_name_et.text.toString().trim().uppercase(Locale.getDefault()),
                    cin_et.text.toString().trim().uppercase(Locale.getDefault()),
                    "https://firebasestorage.googleapis.com/v0/b/absence-management-app-465ef.appspot.com/o/profile_images%2Fprofile.png?alt=media&token=b6cfb05f-1667-48a2-8f1d-c6e530df8d11",
                    cne_et.text.toString().trim().uppercase(Locale.getDefault()),
                    filiere_dropdown.text.toString().trim().uppercase(Locale.getDefault()),
                    semester_dropdown.text.toString().trim().uppercase(Locale.getDefault()),
                    email_et.text.toString()
                )
                database = FirebaseDatabase.getInstance()
                val ref = database.getReference("students")
                val id = FirebaseAuth.getInstance().currentUser!!.uid
                ref.child(id).setValue(student).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        this.context?.let {
                            FancyToast.makeText(
                                it,
                                "You have been registered successfully",
                                FancyToast.LENGTH_LONG,
                                FancyToast.SUCCESS,
                                false
                            ).show()
                        }
                        redirectToLogin()
                    } else {
                        FancyToast.makeText(
                            requireContext(),
                            "Error: ${task.exception?.message}",
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show()
                    }
                }
            } else {
                FancyToast.makeText(
                    requireContext(),
                    "Error: ${task.exception?.message}",
                    FancyToast.LENGTH_LONG,
                    FancyToast.ERROR,
                    false
                ).show()
            }
        }

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
        login_tv = view?.findViewById(R.id.login_tv)!!
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
                first_name_et.requestFocus()
                false
            }
            last_name.isEmpty() -> {
                last_name_et.error = "Last name is required"
                last_name_et.requestFocus()
                false
            }
            cin.isEmpty() -> {
                cin_et.error = "CIN is required"
                cin_et.requestFocus()
                false
            }
            cne.isEmpty() -> {
                cne_et.error = "CNE is required"
                cne_et.requestFocus()
                false
            }
            filiere.isEmpty() -> {
                filiere_dropdown.error = "Filiere is required"
                filiere_dropdown.requestFocus()
                false
            }
            semester.isEmpty() -> {
                semester_dropdown.error = "Semester is required"
                semester_dropdown.requestFocus()
                false
            }
            email.isEmpty() -> {
                email_et.error = "Email is required"
                email_et.requestFocus()
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                email_et.error = "Email is not valid"
                email_et.requestFocus()
                false
            }
            password.isEmpty() -> {
                password_et.error = "Password is required"
                password_et.requestFocus()
                false
            }
            password.length < 6 -> {
                password_et.error = "Password must be at least 6 characters"
                password_et.requestFocus()
                false
            }
            confirm_password.isEmpty() -> {
                confirm_password_et.error = "Confirm password is required"
                confirm_password_et.requestFocus()
                false
            }
            password != confirm_password -> {
                confirm_password_et.error = "Passwords don't match"
                confirm_password_et.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun redirectToLogin() {
        Intent(activity!!.applicationContext, LoginActivity::class.java).also {
            startActivity(it)
        }
    }
}
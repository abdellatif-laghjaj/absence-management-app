package com.example.absencemanagementapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import com.example.absencemanagementapp.LoginActivity
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.models.Student
import com.example.absencemanagementapp.models.Teacher
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.*

class TeacherRegisterFragment : Fragment() {
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

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //put the code here
        initViews()
        register_btn.setOnClickListener {
            if (validateInputs()) {
                //register the teacher
                val email = email_et.text.toString().trim()
                val password = password_et.text.toString().trim()
                registerTeacher(email, password)
            }
        }
    }

    private fun registerTeacher(email: String, password: String) {
        //register the teacher to firebase
        auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //register teacher to database
                val teacher = Teacher(
                    first_name_et.text.toString().trim(),
                    last_name_et.text.toString().trim(),
                    cin_et.text.toString().trim(),
                    email_et.text.toString().trim()
                )
                database = FirebaseDatabase.getInstance()
                val ref = database.getReference("teachers")
                val id = FirebaseAuth.getInstance().currentUser!!.uid
                ref.child(id).setValue(teacher).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FancyToast.makeText(
                            requireContext(),
                            "You have been registered successfully",
                            Toast.LENGTH_SHORT,
                            FancyToast.SUCCESS,
                            false
                        ).show()
                        redirectToLogin()
                    } else {
                        FancyToast.makeText(
                            requireContext(),
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_SHORT,
                            FancyToast.ERROR,
                            false
                        ).show()
                    }
                }
            } else {
                FancyToast.makeText(
                    requireContext(),
                    "Error: ${task.exception?.message}",
                    Toast.LENGTH_SHORT,
                    FancyToast.ERROR,
                    false
                ).show()
            }
        }
    }

    private fun redirectToLogin() {
        Intent(requireContext(), LoginActivity::class.java).also {
            startActivity(it)
            requireActivity().finish()
        }
    }

    private fun initViews() {
        first_name_et = view?.findViewById(R.id.first_name_et)!!
        last_name_et = view?.findViewById(R.id.last_name_et)!!
        cin_et = view?.findViewById(R.id.cin_et)!!
        email_et = view?.findViewById(R.id.email_et)!!
        password_et = view?.findViewById(R.id.password_et)!!
        confirm_password_et = view?.findViewById(R.id.confirm_password_et)!!
        register_btn = view?.findViewById(R.id.register_btn)!!
    }

    private fun validateInputs(): Boolean {
        val first_name = first_name_et.text.toString()
        val last_name = last_name_et.text.toString()
        val cin = cin_et.text.toString()
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
            email.isEmpty() -> {
                email_et.error = "Email is required"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                email_et.error = "Email is not valid"
                false
            }
            password.isEmpty() -> {
                password_et.error = "Password is required"
                false
            }
            password.length < 6 -> {
                password_et.error = "Password must be at least 6 characters"
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

    private fun switchToLogin() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }
}
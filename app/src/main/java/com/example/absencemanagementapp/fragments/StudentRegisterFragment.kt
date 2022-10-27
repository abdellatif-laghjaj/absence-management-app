package com.example.absencemanagementapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.absencemanagementapp.R
import com.google.android.material.textfield.TextInputEditText

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
        register_btn.setOnClickListener {
            val first_name = first_name_et.text.toString()
            Toast.makeText(context, first_name, Toast.LENGTH_SHORT).show()
        }
        filiere_dropdown.setOnItemClickListener { adapterView, _, i, _ ->
            Toast.makeText(
                context,
                "Selected branch is ${adapterView.getItemAtPosition(i)}",
                Toast.LENGTH_SHORT
            ).show()
        }

        semester_dropdown.setOnItemClickListener { adapterView, _, i, _ ->
            Toast.makeText(
                context,
                "Selected semester is ${adapterView.getItemAtPosition(i)}",
                Toast.LENGTH_SHORT
            ).show()
        }
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

    private fun initDropDowns() {
        val filiere_adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, branches)
        filiere_dropdown.setAdapter(filiere_adapter)
        val semester_adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, semsters)
        semester_dropdown.setAdapter(semester_adapter)
    }
}
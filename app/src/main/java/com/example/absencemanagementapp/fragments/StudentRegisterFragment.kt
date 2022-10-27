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
        initDropDowns()
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

    private fun initDropDowns() {
        val filiere_adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, branches)
        filiere_dropdown.setAdapter(filiere_adapter)
        val semester_adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, semsters)
        semester_dropdown.setAdapter(semester_adapter)
    }
}
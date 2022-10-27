package com.example.absencemanagementapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.absencemanagementapp.LoginActivity
import com.example.absencemanagementapp.R
import com.google.android.material.textfield.TextInputEditText

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //put the code here
        initViews()
        register_btn.setOnClickListener {
            val first_name = first_name_et.text.toString()
            Toast.makeText(context, first_name, Toast.LENGTH_LONG).show()
        }
    }

    private fun initViews() {
        first_name_et = view?.findViewById(R.id.first_name_et)!!
        last_name_et = view?.findViewById(R.id.last_name_et)!!
        email_et = view?.findViewById(R.id.email_et)!!
        password_et = view?.findViewById(R.id.password_et)!!
        confirm_password_et = view?.findViewById(R.id.confirm_password_et)!!
        register_btn = view?.findViewById(R.id.register_btn)!!
    }

    private fun switchToLogin() {
        val intent = Intent(context, LoginActivity::class.java)
        startActivity(intent)
    }
}
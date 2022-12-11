package com.example.absencemanagementapp.activities.auth

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.activities.home.StudentActivity
import com.example.absencemanagementapp.activities.home.TeacherActivity
import com.example.absencemanagementapp.helpers.Helper
import com.example.absencemanagementapp.helpers.Helper.Companion.checkInternetConnection
import com.example.absencemanagementapp.helpers.Helper.Companion.isConnected
import com.example.absencemanagementapp.helpers.Helper.Companion.showExitDialog
import com.example.absencemanagementapp.models.Student
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import java.util.*

class LoginActivity : AppCompatActivity() {
    private lateinit var forget_password_tv: TextView
    private lateinit var register_tv: TextView
    private lateinit var email_et: TextInputEditText
    private lateinit var password_et: TextInputEditText
    private lateinit var login_btn: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //initiate views
        initViews()

        register_tv.setOnClickListener {
            Intent(this, RegisterActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        login_btn.setOnClickListener {
            val email = email_et.text.toString().trim().uppercase(Locale.getDefault())
            val password = password_et.text.toString().trim()

            if (validateInputs()) {
                login(email, password)
            }
        }

        forget_password_tv.setOnClickListener {
            showResetPasswordDialog()
        }
    }

    override fun onBackPressed() {
        showExitDialog(this)
    }

    private fun login(email: String, password: String) {
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle(getString(R.string.logging_in))
        progressDialog.setMessage(getString(R.string.logging_you_to_space))

        //log in the user
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = auth.currentUser
                //check if logged in user is a student or a teacher
                database.getReference("students").child(user!!.uid).get().addOnSuccessListener {
                    if (it.exists()) {
                        progressDialog.dismiss()
                        //user is a student
                        val student = it.getValue(Student::class.java)
                        Intent(this, StudentActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    } else {
                        progressDialog.dismiss()
                        //user is a teacher
                        Intent(this, TeacherActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    }
                }
            } else {
                progressDialog.dismiss()
                // If sign in fails, display a dialog to the user.
                val dialog = MaterialDialog.Builder(this)
                    .setTitle(getString(R.string.login_failed))
                    .setAnimation(R.raw.invalid)
                    .setMessage(getString(R.string.invalid_email_or_password))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .build()
                dialog.show()

                val animationView: LottieAnimationView = dialog.getAnimationView()

                //scale animation view
                animationView.scaleX = 0.5f
                animationView.scaleY = 0.5f
            }
        }
    }

    private fun initViews() {
        forget_password_tv = findViewById(R.id.forget_password_tv)
        register_tv = findViewById(R.id.register_tv)
        email_et = findViewById(R.id.email_et)
        password_et = findViewById(R.id.password_et)
        login_btn = findViewById(R.id.login_btn)
    }

    private fun validateInputs(): Boolean {
        val email = email_et.text.toString()
        val password = password_et.text.toString()
        return when {
            email.isEmpty() -> {
                email_et.error = getString(R.string.email_required)
                email_et.requestFocus()
                false
            }
            password.isEmpty() -> {
                password_et.error = getString(R.string.password_required)
                password_et.requestFocus()
                false
            }
            else -> true
        }
    }

    override fun onStart() {
        super.onStart()
        //check if user is connected to the internet
        if (!isConnected(this)) {
            val dialog_not_internet = MaterialDialog.Builder(this)
                .setTitle(getString(R.string.no_internet_connection))
                .setAnimation(R.raw.no_internet)
                .setMessage(getString(R.string.please_check_your_connection))
                .setNegativeButton(getString(R.string.exit)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                    finish()
                }
                .setPositiveButton(getString(R.string.ok)) { _, _ ->
                    checkInternetConnection(this, this)
                }
                .build()
            dialog_not_internet.show()

            val animationView: LottieAnimationView = dialog_not_internet.getAnimationView()

            //scale animation view
            animationView.scaleX = 0.5f
            animationView.scaleY = 0.5f
        }
    }

    private fun showResetPasswordDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.reset_password_bottom_sheet_layout)
        val email_et = dialog.findViewById<TextInputEditText>(R.id.email_et)
        val reset_password_btn = dialog.findViewById<Button>(R.id.reset_password_btn)

        reset_password_btn.setOnClickListener {
            val email = email_et.text.toString()
            if (email.isEmpty()) {
                email_et.error = getString(R.string.email_required)
                email_et.requestFocus()
                return@setOnClickListener
            }
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    dialog.dismiss()
                    val dialog_success =
                        MaterialDialog.Builder(this).setTitle(getString(R.string.email_sent))
                            .setMessage(getString(R.string.password_reset_link_sent))
                            .setAnimation(R.raw.success)
                            .setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }.build()

                    dialog_success.show()

                    val animationView: LottieAnimationView = dialog_success.getAnimationView()

                    //scale animation
                    animationView.scaleX = 0.5f
                    animationView.scaleY = 0.5f
                } else {
                    val dialog_failed =
                        MaterialDialog.Builder(this).setTitle(getString(R.string.email_not_sent))
                            .setMessage(getString(R.string.failed_to_send_password_reset_link))
                            .setAnimation(R.raw.failed)
                            .setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }.build()
                    dialog_failed.show()

                    val animationView: LottieAnimationView = dialog_failed.getAnimationView()

                    //scale animation
                    animationView.scaleX = 0.5f
                    animationView.scaleY = 0.5f
                }
            }
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
        dialog.show()
    }
}
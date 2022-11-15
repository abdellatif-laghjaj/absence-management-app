package com.example.absencemanagementapp.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.models.Student
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import dev.shreyaspatil.MaterialDialog.MaterialDialog

class StudentActivity : AppCompatActivity() {
    private lateinit var student_image_civ: CircleImageView
    private lateinit var user_name_tv: TextView
    private lateinit var scan_qr_code_cv: CardView
    private lateinit var profile_cv: CardView
    private lateinit var reset_password_cv: CardView
    private lateinit var logout_cv: CardView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        //initiate views
        initViews()

        //get user name
        val user_id = auth.currentUser!!.uid
        database.getReference("students").child(user_id).get().addOnSuccessListener {
            if (it.exists()) {
                val student = it.getValue(Student::class.java)
                user_name_tv.text = student!!.first_name
            }
        }

        //get user image
        database.getReference("students").child(user_id).child("avatar").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val image = it.value.toString()
                    student_image_civ.setImageResource(
                        resources.getIdentifier(
                            image,
                            "drawable",
                            packageName
                        )
                    )
                }
            }

        //dashboard cards handling
        reset_password_cv.setOnClickListener {
            showResetPasswordDialog()
        }

        logout_cv.setOnClickListener {
            logout()
        }

        profile_cv.setOnClickListener {
            Intent(this, StudentProfileActivity::class.java).also {
                startActivity(it)
            }
        }

        student_image_civ.setOnClickListener {
            Intent(this, StudentProfileActivity::class.java).also {
                startActivity(it)
            }
        }

        scan_qr_code_cv.setOnClickListener {
            Intent(this, ScanQrCodeActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    //logout
    private fun logout() {
        val dialog = MaterialDialog.Builder(this).setTitle("Logout")
            .setMessage("Are you sure you want to logout?").setCancelable(false)
            .setAnimation(R.raw.logout).setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                redirectToLogin()
            }.setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.build()
        dialog.show()

        val animationView: LottieAnimationView = dialog.getAnimationView()

        //scale animation
        animationView.scaleX = 0.5f
        animationView.scaleY = 0.5f
    }

    //redirect to login activity
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initViews() {
        student_image_civ = findViewById(R.id.student_image_civ)
        user_name_tv = findViewById(R.id.user_name_tv)
        logout_cv = findViewById(R.id.logout_cv)
        scan_qr_code_cv = findViewById(R.id.scan_qr_code_cv)
        profile_cv = findViewById(R.id.profile_cv)
        reset_password_cv = findViewById(R.id.reset_password_cv)
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
                email_et.error = "Email is required"
                email_et.requestFocus()
                return@setOnClickListener
            }
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    dialog.dismiss()
                    val email_sent_dialog = MaterialDialog.Builder(this).setTitle("Reset Password")
                        .setAnimation(R.raw.success)
                        .setMessage("Password reset link has been sent to your email. If you don't see the email, please check your spam folder.")
                        .setPositiveButton("Ok") { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            //logout
                            auth.signOut()
                            redirectToLogin()
                        }.build()
                    email_sent_dialog.show()

                    //scale animation
                    val animationView: LottieAnimationView = email_sent_dialog.getAnimationView()
                    animationView.scaleX = 0.5f
                    animationView.scaleY = 0.5f
                } else {
                    val email_not_sent_dialog =
                        MaterialDialog.Builder(this).setTitle("Reset Password")
                            .setMessage("Failed to send password reset link")
                            .setAnimation(R.raw.failed)
                            .setPositiveButton("Ok") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }.build()
                    email_not_sent_dialog.show()

                    //scale animation
                    val animationView: LottieAnimationView =
                        email_not_sent_dialog.getAnimationView()
                    animationView.scaleX = 0.5f
                    animationView.scaleY = 0.5f
                }
            }
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.attributes?.windowAnimations = R.style.BottomSheetDialogAnimation
        dialog.show()
    }
}
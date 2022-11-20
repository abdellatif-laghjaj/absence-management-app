package com.example.absencemanagementapp.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.example.absencemanagementapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import dev.shreyaspatil.MaterialDialog.MaterialDialog

class SettingsActivity : AppCompatActivity() {
    private lateinit var reset_password_cv: ImageView
    private lateinit var logout_cv: ImageView
    private lateinit var back_iv: ImageView
    private lateinit var bottom_navigation: BottomNavigationView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        //put the code here
        initViews()

        //set dashboard selected
        bottom_navigation.selectedItemId = R.id.settings
        //set bottom navigation listener
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard -> {
                    startActivity(Intent(this, StudentActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.scan_qr_code -> {
                    startActivity(Intent(this, ScanQrCodeActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, StudentProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.settings -> {
                    true
                }
                else -> false
            }
        }

        back_iv.setOnClickListener {
            finish()
        }
    }

    //show About dialog
    private fun showAboutDialog() {
        val dialog = MaterialDialog.Builder(this)
            .setTitle("About")
            .setMessage("Absence Management App is an app that allows students to scan QR codes to mark their attendance. It also allows teachers to view the attendance of their students.")
            .setPositiveButton("OK", R.drawable.ic_done) { dialogInterface, which ->
                dialogInterface.dismiss()
            }
            .setAnimation(R.raw.about)
            .build()
        dialog.show()

        val animationView: LottieAnimationView = dialog.animationView

        //scale animation
        animationView.scaleX = 0.5f
        animationView.scaleY = 0.5f
    }

    //show Credits dialog
    private fun showCreditsDialog() {
        val dialog = MaterialDialog.Builder(this)
            .setTitle("Credits")
            .setMessage("We will put credits here")
            .setPositiveButton("OK", R.drawable.ic_done) { dialogInterface, which ->
                dialogInterface.dismiss()
            }
            .setAnimation(R.raw.credits)
            .build()
        dialog.show()

        val animationView: LottieAnimationView = dialog.animationView

        //scale animation
        animationView.scaleX = 0.5f
        animationView.scaleY = 0.5f
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

        val animationView: LottieAnimationView = dialog.animationView

        //scale animation
        animationView.scaleX = 0.5f
        animationView.scaleY = 0.5f
    }

    //show reset password dialog
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

    //redirect to login activity
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun initViews() {
        bottom_navigation = findViewById(R.id.bottom_navigation)
        logout_cv = findViewById(R.id.logout_iv)
        back_iv = findViewById(R.id.back_iv)
        reset_password_cv = findViewById(R.id.reset_password_iv)
    }
}
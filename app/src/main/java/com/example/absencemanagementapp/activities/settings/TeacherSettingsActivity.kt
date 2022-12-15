package com.example.absencemanagementapp.activities.settings

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatDelegate
import com.airbnb.lottie.LottieAnimationView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.activities.auth.LoginActivity
import com.example.absencemanagementapp.activities.home.TeacherActivity
import com.example.absencemanagementapp.activities.profile.TeacherProfileActivity
import com.example.absencemanagementapp.helpers.Helper.Companion.changeLanguage
import com.example.absencemanagementapp.helpers.Helper.Companion.changeTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dev.shreyaspatil.MaterialDialog.MaterialDialog

class TeacherSettingsActivity : AppCompatActivity() {
    private lateinit var change_language_layout: RelativeLayout
    private lateinit var change_theme_layout: RelativeLayout
    private lateinit var reset_password_layout: RelativeLayout
    private lateinit var logout_layout: RelativeLayout
    private lateinit var about_layout: RelativeLayout
    private lateinit var credits_layout: RelativeLayout
    private lateinit var back_iv: ImageView
    private lateinit var bottom_navigation: BottomNavigationView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_settings)

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
                    startActivity(Intent(this, TeacherActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, TeacherProfileActivity::class.java))
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

        change_language_layout.setOnClickListener {
            showChangeLanguageDialog()
        }

        change_theme_layout.setOnClickListener {
            showChangeThemeDialog()
        }

        logout_layout.setOnClickListener {
            logout()
        }

        reset_password_layout.setOnClickListener {
            showResetPasswordDialog()
        }

        credits_layout.setOnClickListener {
            showCreditsDialog()
        }

        about_layout.setOnClickListener {
            showAboutDialog()
        }
    }

    override fun onBackPressed() {
        bottom_navigation.selectedItemId = R.id.profile
        startActivity(Intent(this, TeacherActivity::class.java))
        finish()
    }

    //show change language dialog
    private fun showChangeLanguageDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_change_language)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)

        val confirm_btn = dialog.findViewById<Button>(R.id.confirm_btn)
        val language_rg = dialog.findViewById<RadioGroup>(R.id.language_rg)

        //change language
        confirm_btn.setOnClickListener {
            when (language_rg.checkedRadioButtonId) {
                R.id.rb_english_language -> {
                    //change language to english
                    changeLanguage("en", this)
                    dialog.dismiss()
                    restartActivity()
                }
                R.id.rb_frensh_language -> {
                    //change language to frensh
                    changeLanguage("fr", this)
                    dialog.dismiss()
                    restartActivity()
                }
                R.id.rb_arabic_language -> {
                    //change language to arabic
                    changeLanguage("ar", this)
                    dialog.dismiss()
                    restartActivity()
                }
                else -> {
                    //change language to english
                    changeLanguage("en", this)
                    dialog.dismiss()
                    restartActivity()
                }
            }
        }

        dialog.show()
    }

    //show change theme dialog
    private fun showChangeThemeDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_change_theme)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)

        val cancel_btn = dialog.findViewById<Button>(R.id.cancel_btn)
        val theme_rg = dialog.findViewById<RadioGroup>(R.id.theme_rg)

        //change theme
        theme_rg.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_light_theme -> {
                    //set light theme
                    changeTheme("light", this)
                }
                R.id.rb_dark_theme -> {
                    //set dark theme
                    changeTheme("dark", this)
                }
            }
        }

        cancel_btn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //show About dialog
    private fun showAboutDialog() {
        val dialog = MaterialDialog.Builder(this)
            .setTitle(getString(R.string.about))
            .setMessage(getString(R.string.about_dialog_message))
            .setPositiveButton(
                getString(R.string.ok),
                R.drawable.ic_done
            ) { dialogInterface, which ->
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
            .setTitle(getString(R.string.credits))
            //add links to icons
            .setMessage(getString(R.string.credits_dialog_message))
            .setPositiveButton(
                getString(R.string.ok),
                R.drawable.ic_done
            ) { dialogInterface, which ->
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
        val dialog = MaterialDialog.Builder(this).setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_dialog_message))
            .setCancelable(false)
            .setAnimation(R.raw.logout).setPositiveButton(getString(R.string.yes)) { _, _ ->
                auth.signOut()
                //redirect to login activity
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }.setNegativeButton(getString(R.string.no)) { dialogInterface, _ ->
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
                email_et.error = getString(R.string.email_required)
                email_et.requestFocus()
                return@setOnClickListener
            }
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    dialog.dismiss()
                    val email_sent_dialog = MaterialDialog.Builder(this).setTitle(getString(R.string.email_sent))
                        .setAnimation(R.raw.success)
                        .setMessage(getString(R.string.password_reset_link_sent))
                        .setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                            dialogInterface.dismiss()
                            //logout
                            auth.signOut()
                            redirectToLogin()
                        }.build()
                    email_sent_dialog.show()

                    //scale animation
                    val animationView: LottieAnimationView = email_sent_dialog.animationView
                    animationView.scaleX = 0.5f
                    animationView.scaleY = 0.5f
                } else {
                    val email_not_sent_dialog =
                        MaterialDialog.Builder(this).setTitle(getString(R.string.email_not_sent))
                            .setMessage(getString(R.string.failed_to_send_password_reset_link))
                            .setAnimation(R.raw.failed)
                            .setPositiveButton(getString(R.string.ok)) { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }.build()
                    email_not_sent_dialog.show()

                    //scale animation
                    val animationView: LottieAnimationView =
                        email_not_sent_dialog.animationView
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
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun initViews() {
        back_iv = findViewById(R.id.back_iv)
        bottom_navigation = findViewById(R.id.bottom_navigation)
        change_language_layout = findViewById(R.id.change_language_layout)
        change_theme_layout = findViewById(R.id.change_theme_layout)
        logout_layout = findViewById(R.id.logout_layout)
        reset_password_layout = findViewById(R.id.reset_password_layout)
        credits_layout = findViewById(R.id.credits_layout)
        about_layout = findViewById(R.id.about_layout)
    }

    private fun restartActivity() {
        if (Build.VERSION.SDK_INT >= 11) {
            recreate()
        } else {
            val intent = intent
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}
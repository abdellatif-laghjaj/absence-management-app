package com.example.absencemanagementapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.adapters.ModulesAdapter
import com.example.absencemanagementapp.models.Module
import com.example.absencemanagementapp.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dev.shreyaspatil.MaterialDialog.MaterialDialog

class TeacherActivity : AppCompatActivity() {
    private lateinit var user_name_tv: TextView
    private lateinit var logout_cv: CardView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher);

        //region: Display all modules of teacher
        val rv = findViewById<RecyclerView>(R.id.recyclerview);
        rv.layoutManager = LinearLayoutManager(this);
        val modules = ArrayList<Module>();
        modules.add(Module(1, "Algebre 1", "ALG1", 1, 9))
        modules.add(Module(2, "Analyse 1", "ALG1", 1, 9))
        modules.add(Module(3, "Physique 1", "ALG1", 1, 9))
        modules.add(Module(4, "Probabilité statistique", "ALG1", 1, 9))
        modules.add(Module(5, "Algorithmique et programmation 1", "ALG1", 1, 9))
        modules.add(Module(6, "Langues et terminologie 1", "ALG1", 1, 9))
        modules.add(Module(7, "Environnement d'entreprise", "ALG1", 1, 9))
        val modulesAdapter = ModulesAdapter(modules, this);
        rv.adapter = modulesAdapter;
        //endregion

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

        //dashboard cards handling
//        logout_cv.setOnClickListener {
//            logout()
            //redirect to login activity
//        }
    }

    private fun initViews() {
        user_name_tv = findViewById(R.id.user_name_tv)
    }

 /*   //logout
    private fun logout() {
        val dialog = MaterialDialog.Builder(this)
            .setTitle("Logout")
            .setAnimation(R.raw.logout)
            .setMessage("Are you sure you want to logout?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                redirectToLogin()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .build()
        dialog.show()

        //scale animation
        val animationView: LottieAnimationView = dialog.getAnimationView()
        animationView.scaleX = 0.5f
        animationView.scaleY = 0.5f
    }
*/
    //redirect to login activity
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
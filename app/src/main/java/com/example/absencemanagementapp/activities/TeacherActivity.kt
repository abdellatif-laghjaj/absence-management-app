package com.example.absencemanagementapp.activities

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.adapters.ModulesAdapter
import com.example.absencemanagementapp.models.Module
import com.example.absencemanagementapp.models.Student
import com.example.absencemanagementapp.models.Teacher
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import de.hdodenhof.circleimageview.CircleImageView
import dev.shreyaspatil.MaterialDialog.MaterialDialog

class TeacherActivity : AppCompatActivity() {
    private lateinit var user_name_tv: TextView
    private lateinit var teacher_image_civ: CircleImageView
    private lateinit var bottom_navigation: BottomNavigationView
    private lateinit var modules_swipe: SwipeRefreshLayout

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher);

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        //initiate views
        initViews()

        //set dashboard selected
        bottom_navigation.selectedItemId = R.id.dashboard
        //set bottom navigation listener
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.dashboard -> {
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, TeacherProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.settings -> {
                    startActivity(Intent(this, TeacherSettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        //get user name
        val user_id = auth.currentUser!!.uid
        database.getReference("teachers").child(user_id).get().addOnSuccessListener {
            if (it.exists()) {
                val teacher = it.getValue(Teacher::class.java)
                user_name_tv.text = teacher!!.last_name
            }
        }

        //get user image
        database.getReference("teachers").child(user_id).child("avatar").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    //get the image
                    val image = it.value.toString()

                    //ask for permission
                    Dexter.withContext(this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(object : PermissionListener {
                            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                                //load the image
                                Glide.with(this@TeacherActivity)
                                    .load(image)
                                    .into(teacher_image_civ)
                            }

                            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                                //show error
                                MaterialDialog.Builder(this@TeacherActivity)
                                    .setTitle("Permission Denied")
                                    .setMessage("You must accept this permission to use this feature")
                                    .setCancelable(false)
                                    .setPositiveButton("OK") { dialogInterface, _ ->
                                        dialogInterface.dismiss()
                                    }
                                    .build()
                                    .show()
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                permission: PermissionRequest?,
                                token: PermissionToken?
                            ) {
                                token!!.continuePermissionRequest()
                            }
                        }).check()
                }
            }

        teacher_image_civ.setOnClickListener {
            Intent(this, TeacherProfileActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun getModules(): List<Module> {
        val modules = ArrayList<Module>();

        modules.add(Module(1, "Algebre 1", "ALG1", 1, "GI"))
        modules.add(Module(2, "Analyse 1", "ALG1", 1, "EE"))
        modules.add(Module(3, "Physique 1", "ALG1", 1, "LEA"))
        modules.add(Module(4, "Probabilité statistique", "ALG1", 1, "GI"))
        modules.add(Module(5, "Algorithmique et programmation 1", "ALG1", 1, "SV"))
        modules.add(Module(6, "Langues et terminologie 1", "ALG1", 1, "EG"))
        modules.add(Module(7, "Environnement d'entreprise", "ALG1", 1, "SGARNE"))

        return modules
    }

    private fun initModules() {
        val rv = findViewById<RecyclerView>(R.id.module_rv);
        rv.layoutManager = LinearLayoutManager(this);
        val modulesAdapter = ModulesAdapter(getModules(), this);
        rv.adapter = modulesAdapter;
    }

    private fun initViews() {
        initModules()
        bottom_navigation = findViewById(R.id.bottom_navigation)
        user_name_tv = findViewById(R.id.user_name_tv)
        teacher_image_civ = findViewById(R.id.teacher_image_civ)
        modules_swipe = this.findViewById(R.id.modules_swipe)
        modules_swipe.setOnRefreshListener {
            initModules()
            modules_swipe.isRefreshing = false
        }
    }

    //redirect to login activity
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
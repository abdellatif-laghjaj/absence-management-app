package com.example.absencemanagementapp.activities.profile

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.activities.ScanQrCodeActivity
import com.example.absencemanagementapp.activities.home.StudentActivity
import com.example.absencemanagementapp.activities.settings.StudentSettingsActivity
import com.example.absencemanagementapp.models.Student
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.shashank.sony.fancytoastlib.FancyToast
import de.hdodenhof.circleimageview.CircleImageView
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream


class StudentProfileActivity : AppCompatActivity() {
    private lateinit var bottom_navigation: BottomNavigationView
    private lateinit var profile_image_picker_btn: ImageButton
    private lateinit var student_profile_image_civ: CircleImageView
    private lateinit var back_iv: ImageView
    private lateinit var user_name_tv: TextView
    private lateinit var user_email_tv: TextView
    private lateinit var first_name_et: TextInputEditText
    private lateinit var last_name_et: TextInputEditText
    private lateinit var cin_et: TextInputEditText
    private lateinit var cne_et: TextInputEditText
    private lateinit var filiere_dropdown: AutoCompleteTextView
    private lateinit var semester_dropdown: AutoCompleteTextView
    private lateinit var update_btn: Button
    private var bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    private val semesters = arrayOf("1", "2", "3", "4", "5", "6")
    private val branches = arrayOf("GI", "SV", "LAE", "ECO")

    private final val REQUEST_CODE = 100
    private lateinit var uri: Uri

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_profile)

        val upload_dialog = Dialog(this)
        upload_dialog.setContentView(R.layout.dialog_uploading)
        upload_dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        upload_dialog.window!!.attributes.windowAnimations = android.R.style.Animation_Dialog

        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        //get user id
        val user_id = auth.currentUser!!.uid

        database.getReference("students").child(user_id).child("avatar").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    //get the image
                    val image = it.value.toString()
                    Glide.with(this).load(image).into(student_profile_image_civ)
                }
            }

        //put the code here
        initViews()
        initDropDowns()
        fillData()

        //set dashboard selected
        bottom_navigation.selectedItemId = R.id.profile
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
                    true
                }
                R.id.settings -> {
                    startActivity(Intent(this, StudentSettingsActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        back_iv.setOnClickListener {
            finish()
        }

        student_profile_image_civ.setOnClickListener {
            //show image in dialog
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_user_image)
            val image = dialog.findViewById<ImageView>(R.id.user_image_iv)
            //get current user image
            database.getReference("students").child(user_id).child("avatar").get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        //get the image
                        val image_url = it.value.toString()
                        Glide.with(this).load(image_url).into(image)
                    }
                }

            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.attributes.windowAnimations = android.R.style.Animation_Dialog
            dialog.show()
        }

        filiere_dropdown.setOnItemClickListener { adapterView, _, i, _ ->
            //adapterView.getItemAtPosition(i)
        }

        semester_dropdown.setOnItemClickListener { adapterView, _, i, _ ->
            //adapterView.getItemAtPosition(i)
        }

        profile_image_picker_btn.setOnClickListener {
            val perms =
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (EasyPermissions.hasPermissions(this, *perms)) {
                //open gallery
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_CODE)
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    "Please accept our permissions",
                    REQUEST_CODE,
                    *perms
                )
            }
        }

        //update logic
        update_btn.setOnClickListener {
            if (validateInputs()) {
                upload_dialog.show()

                val email = getCurrentUserEmail()
                val student = Student(
                    first_name_et.text.toString(),
                    last_name_et.text.toString(),
                    cin_et.text.toString(),
                    uri.toString(),
                    cne_et.text.toString(),
                    filiere_dropdown.text.toString(),
                    semester_dropdown.text.toString(),
                    email
                )

                database.reference.child("students").child(auth.currentUser!!.uid)
                    .setValue(student)
                    .addOnSuccessListener {
                        FancyToast.makeText(
                            this,
                            "Profile updated successfully",
                            FancyToast.LENGTH_SHORT,
                            FancyToast.SUCCESS,
                            false
                        ).show()
                    }
                    .addOnFailureListener {
                        FancyToast.makeText(
                            this,
                            "Error: ${it.message}",
                            FancyToast.LENGTH_SHORT,
                            FancyToast.ERROR,
                            false
                        ).show()
                    }

                //save the image
                val storageRef = storage.reference
                val imageRef = storageRef.child("profile_images/${auth.currentUser!!.uid}.jpg")
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    FancyToast.makeText(
                        this,
                        "Error: ${it.message}",
                        FancyToast.LENGTH_SHORT,
                        FancyToast.ERROR,
                        false
                    ).show()
                    upload_dialog.dismiss()
                }.addOnSuccessListener {
                    FancyToast.makeText(
                        this,
                        "Image uploaded successfully",
                        FancyToast.LENGTH_SHORT,
                        FancyToast.SUCCESS,
                        false
                    ).show()
                    upload_dialog.dismiss()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val first_name = first_name_et.text.toString()
        val last_name = last_name_et.text.toString()
        val cin = cin_et.text.toString()
        val cne = cne_et.text.toString()
        val filiere = filiere_dropdown.text.toString()
        val semester = semester_dropdown.text.toString()
        return when {
            first_name.isEmpty() -> {
                first_name_et.error = "First name is required"
                first_name_et.requestFocus()
                false
            }
            last_name.isEmpty() -> {
                last_name_et.error = "Last name is required"
                last_name_et.requestFocus()
                false
            }
            cin.isEmpty() -> {
                cin_et.error = "CIN is required"
                cin_et.requestFocus()
                false
            }
            cne.isEmpty() -> {
                cne_et.error = "CNE is required"
                cne_et.requestFocus()
                false
            }
            filiere.isEmpty() -> {
                filiere_dropdown.error = "Filiere is required"
                filiere_dropdown.requestFocus()
                false
            }
            semester.isEmpty() -> {
                semester_dropdown.error = "Semester is required"
                semester_dropdown.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun initDropDowns() {
        val filiere_adapter = ArrayAdapter(this, R.layout.dropdown_item, branches)
        filiere_dropdown.setAdapter(filiere_adapter)
        val semester_adapter = ArrayAdapter(this, R.layout.dropdown_item, semesters)
        semester_dropdown.setAdapter(semester_adapter)
    }

    public fun initViews() {
        bottom_navigation = findViewById(R.id.bottom_navigation)
        student_profile_image_civ = findViewById(R.id.teacher_profile_image_civ)
        profile_image_picker_btn = findViewById(R.id.profile_image_picker_btn)
        back_iv = findViewById(R.id.back_iv)
        user_name_tv = findViewById(R.id.user_name_tv)
        user_email_tv = findViewById(R.id.user_email_tv)
        first_name_et = findViewById(R.id.first_name_et)
        last_name_et = findViewById(R.id.last_name_et)
        cin_et = findViewById(R.id.cin_et)
        cne_et = findViewById(R.id.cne_et)
        filiere_dropdown = findViewById(R.id.filiere_dropdown)
        semester_dropdown = findViewById(R.id.semester_dropdown)
        update_btn = findViewById(R.id.update_btn)

        //get current user image
        database.getReference("students").child(auth.currentUser!!.uid).child("avatar").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    //get the image
                    val imageRef = storage.reference.child("profile_images/${auth.currentUser!!.uid}.jpg")
                    imageRef.downloadUrl.addOnSuccessListener {
                        uri = it
                        //Glide.with(this).load(it).into(student_profile_image_civ)
                    }
                }
            }
    }

    public fun fillData() {
        val user = auth.currentUser
        val userRef = database.getReference("students").child(user!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val student = snapshot.getValue(Student::class.java)
                user_name_tv.text = student!!.email
                user_email_tv.text = student.email
                first_name_et.setText(student.first_name)
                last_name_et.setText(student.last_name)
                cin_et.setText(student.cin)
                cne_et.setText(student.cne)
                filiere_dropdown.setText(student.filiere)
                semester_dropdown.setText(student.semester)
            }

            override fun onCancelled(error: DatabaseError) {
                FancyToast.makeText(
                    this@StudentProfileActivity,
                    error.message,
                    FancyToast.LENGTH_SHORT,
                    FancyToast.ERROR,
                    false
                ).show()
            }
        })
    }

    public fun getCurrentUserEmail(): String {
        val user = auth.currentUser
        return user!!.email.toString()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uri = data?.data!!
            student_profile_image_civ.setImageURI(uri)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
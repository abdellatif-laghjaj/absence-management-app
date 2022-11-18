package com.example.absencemanagementapp.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.models.Student
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.shashank.sony.fancytoastlib.FancyToast
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException


class StudentProfileActivity : AppCompatActivity() {
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
    private lateinit var bitmap: Bitmap

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
            Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        //open gallery
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, REQUEST_CODE)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }
                }).check()
        }

        //update logic
        update_btn.setOnClickListener {
            if (validateInputs()) {
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
                val imageRef = storageRef.child("profile_images/${auth.currentUser!!.uid}")
                imageRef.putFile(uri)
                    .addOnSuccessListener {
                        FancyToast.makeText(
                            this,
                            "Image uploaded successfully",
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
        student_profile_image_civ = findViewById(R.id.student_profile_image_civ)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            uri = data.data!!
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                student_profile_image_civ.setImageBitmap(bitmap)
            } catch (e: IOException) {
                FancyToast.makeText(
                    this,
                    "Error: ${e.message}",
                    FancyToast.LENGTH_SHORT,
                    FancyToast.ERROR,
                    false
                ).show()
                e.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadImageToFirebaseStorage(uri: Uri?) {
        FancyToast.makeText(
            this,
            "Uploading image...",
            FancyToast.LENGTH_SHORT,
            FancyToast.INFO,
            false
        ).show()
    }
}
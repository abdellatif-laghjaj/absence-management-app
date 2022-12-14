package com.example.absencemanagementapp.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.helpers.Helper.Companion.formatStudentName
import com.example.absencemanagementapp.helpers.Helper.Companion.shorten
import com.example.absencemanagementapp.models.Absence
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AbsenceAdapter(
    private val absence_list: List<Absence>,
    val context: Context,
    val database: FirebaseDatabase
) :
    RecyclerView.Adapter<AbsenceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val student_image: CircleImageView = itemView.findViewById(R.id.student_image);
        val student_name: TextView = itemView.findViewById(R.id.student_name);

        //val absence_checkbox: CheckBox = itemView.findViewById(R.id.absence_checkbox);
        //val absence_date: TextView = itemView.findViewById(R.id.absence_date);
        val absence_layout = itemView.findViewById<View>(R.id.absence_layout);
        val absence_value: TextView = itemView.findViewById(R.id.absence_text);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.absence_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val absence = absence_list[position];

        database.getReference("students").get().addOnSuccessListener {
            for (student in it.children) {
                if (student.child("cne").value.toString().equals(absence.cne)) {
//                  get student name from db by absence.cne
                    holder.student_name.text = shorten(
                        formatStudentName(
                            student.child("last_name").value.toString(),
                            student.child("first_name").value.toString()
                        ), 24
                    )
//                  set student image URL with picasso
                    if (student.child("avatar").value != null && student.child("avatar").value.toString()
                            .isNotEmpty()
                    ) {
                        Picasso.with(context).load(student.child("avatar").value.toString())
                            .into(holder.student_image)
                    }
                    break;
                }
            }
        }

        if (absence.is_present) {
            holder.absence_value.text = "present";
            holder.absence_layout.setBackgroundResource(R.drawable.present_bg);
        } else {
            holder.absence_value.text = "absent";
            holder.absence_layout.setBackgroundResource(R.drawable.absent_bg);
        }

        //click on item
        holder.itemView.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.absence_item_dialog)
            //full width
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setGravity(Gravity.CENTER)

            val student_image_iv: ImageView = dialog.findViewById(R.id.student_image_iv)
            val student_name_tv: TextView = dialog.findViewById(R.id.student_name_tv)
            val cne_tv: TextView = dialog.findViewById(R.id.cne_tv)
            val branch_tv: TextView = dialog.findViewById(R.id.branch_tv)

            database.getReference("students").get().addOnSuccessListener {
                for (student in it.children) {
                    if (student.child("cne").value.toString().equals(absence.cne)) {
//                      get student name from db by absence.cne
                        student_name_tv.text = shorten(
                            formatStudentName(
                                student.child("last_name").value.toString(),
                                student.child("first_name").value.toString()
                            ), 48
                        )
//                      set student image URL with picasso
                        if (student.child("avatar").value != null && student.child("avatar").value.toString()
                                .isNotEmpty()
                        ) {
                            Picasso.with(context).load(student.child("avatar").value.toString())
                                .into(student_image_iv)
                        }
//                      set student cne
                        cne_tv.text = absence.cne
//                      set student branch
                        branch_tv.text = student.child("filiere").value.toString()
                        break;
                    }
                }
            }

            val absence_status_switch: Switch = dialog.findViewById(R.id.absence_status_switch)

            absence_status_switch.isChecked = absence.is_present;

            absence_status_switch.setOnClickListener {
                absence_status_switch.isChecked = !absence_status_switch.isChecked
                absence.is_present = !absence.is_present
                database.getReference("absences/" + absence.id + "/_present")
                    .setValue(absence_status_switch.isChecked)
            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return absence_list.size;
    }
}
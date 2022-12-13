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
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.helpers.Helper.Companion.formatStudentName
import com.example.absencemanagementapp.models.Absence
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
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

//        get student name from db by absence.cne
        database.getReference("students").get().addOnSuccessListener {
            for (student in it.children) {
                if (student.child("cne").value.toString().equals(absence.cne)) {
                    holder.student_name.text = formatStudentName(student.child("last_name").value.toString(), student.child("first_name").value.toString())
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

            val absence_status_switch: Switch = dialog.findViewById(R.id.absence_status_switch);

            absence_status_switch.isChecked = absence.is_present;

            absence_status_switch.setOnClickListener {
                absence_status_switch.isChecked = !absence_status_switch.isChecked

                val d = MaterialAlertDialogBuilder(context).setTitle("Confirmation")
                    .setMessage("Are you sure you want to change the absence state of student ${absence.cne} ?")
                    .setPositiveButton("Yes") { dialog, which ->
                        absence_status_switch.isChecked = !absence_status_switch.isChecked
                        //change the state
                        absence.is_present = !absence.is_present;

                        database.getReference("absences/" + absence.id).setValue(absence)

                        //upda
                        notifyItemChanged(position)
                    }.setNegativeButton("No") { dialog, which ->
//                        do something
                    }
                d.show()
            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return absence_list.size;
    }
}
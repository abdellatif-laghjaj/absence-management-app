package com.example.absencemanagementapp.adapters

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.models.Absence
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.hdodenhof.circleimageview.CircleImageView

class AbsenceAdapter(private val absence_list: List<Absence>, val context: Context) :
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
        holder.student_name.text = absence.cne;
        if (absence.is_present) {
            holder.absence_value.text = "present";
            holder.absence_layout.setBackgroundResource(R.drawable.present_bg);
        } else {
            holder.absence_value.text = "absent";
            holder.absence_layout.setBackgroundResource(R.drawable.absent_bg);
        }
        //holder.absence_checkbox.isChecked = absence.is_present;
        //holder.absence_date.text = absence.date;
        //add animation to list
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(
                context, R.anim.recycler_view_anim
            )
        )

        //click on item
        holder.itemView.setOnClickListener {
            val dialog = Dialog(context)
            dialog.setContentView(R.layout.absence_item_dialog)
            //full width
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.show()
        }

        //ask for confirmation before marking absence
//        holder.absence_checkbox.setOnClickListener {
//            holder.absence_checkbox.isChecked = !holder.absence_checkbox.isChecked;
//            val dialog = MaterialAlertDialogBuilder(context)
//                .setTitle("Confirmation")
//                .setMessage("Are you sure you want to change the absence state of student ${absence.cne} ?")
//                .setPositiveButton("Yes") { dialog, which ->
//                    //change the state
//                    holder.absence_checkbox.isChecked = !holder.absence_checkbox.isChecked
//                }
//                .setNegativeButton("No") { dialog, which ->
//                    //do nothing
//                }
//            dialog.show()
//        }
    }

    override fun getItemCount(): Int {
        return absence_list.size;
    }
}
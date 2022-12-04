package com.example.absencemanagementapp.adapters

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
import de.hdodenhof.circleimageview.CircleImageView

class AbsenceAdapter(private val absence_list: List<Absence>, val context: Context) :
    RecyclerView.Adapter<AbsenceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val student_image: CircleImageView = itemView.findViewById(R.id.student_image);
        val student_name: TextView = itemView.findViewById(R.id.student_name);
        val absence_checkbox: CheckBox = itemView.findViewById(R.id.absence_checkbox);
        //val absence_date: TextView = itemView.findViewById(R.id.absence_date);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.absence_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val absence = absence_list[position];
        holder.student_name.text = absence.cne;
        holder.absence_checkbox.isChecked = absence.is_present;
        //holder.absence_date.text = absence.date;

        //add animation to list
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(
                context, R.anim.recycler_view_anim
            )
        )
    }

    override fun getItemCount(): Int {
        return absence_list.size;
    }
}
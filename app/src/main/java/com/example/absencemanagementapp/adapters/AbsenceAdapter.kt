package com.example.absencemanagementapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.models.Absence

class AbsenceAdapter(private val absence_list: List<Absence>, val context: Context) :
    RecyclerView.Adapter<AbsenceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val student_image: ImageView = itemView.findViewById(R.id.student_image);
//        val absence_name: TextView = itemView.findViewById(R.id.absence_name);
//        val absence_date: TextView = itemView.findViewById(R.id.absence_date);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val absence = absence_list[position];
//        holder.absence_etudiant.text = absence.etudiant;
//        holder.absence_date.text = absence.date;
//
//        //add animation to list
//        holder.itemView.startAnimation(
//            AnimationUtils.loadAnimation(
//                context, R.anim.recycler_view_anim
//            )
//        )
    }

    override fun getItemCount(): Int {
        return absence_list.size;
    }
}
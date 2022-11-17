package com.example.absencemanagementapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.activities.LoginActivity
import com.example.absencemanagementapp.activities.RegisterActivity
import com.example.absencemanagementapp.activities.TeacherActivity
import com.example.absencemanagementapp.models.Module

class ModulesAdapter(private val mList: List<Module>) : RecyclerView.Adapter<ModulesAdapter.ViewHolder>()  {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var module_intitule: TextView = itemView.findViewById(R.id.module_intitule);
        val module_semestre: TextView = itemView.findViewById(R.id.module_semestre);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var module = mList[position];
        holder.module_intitule.setText(module.inititule);
        holder.module_semestre.setText(module.semestre.toString());
        var id = mList[position].id
        holder.itemView.setOnClickListener({

        })
    }

    override fun getItemCount(): Int {
        return mList.size;
    }
}

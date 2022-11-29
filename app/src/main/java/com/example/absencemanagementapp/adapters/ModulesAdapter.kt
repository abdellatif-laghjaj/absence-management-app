package com.example.absencemanagementapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.activities.ModuleActivity
import com.example.absencemanagementapp.models.Module

class ModulesAdapter(private val modules: List<Module>, val context: Context) :
    RecyclerView.Adapter<ModulesAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var module_intitule: TextView = itemView.findViewById(R.id.module_intitule);
        val module_semestre: TextView = itemView.findViewById(R.id.module_semestre);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val module = modules[position];
        holder.module_intitule.text = module.intitule;
        holder.module_semestre.text = module.formation + " " + module.semestre.toString();

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ModuleActivity::class.java)
            intent.putExtra("module_id", module.id)
            startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return modules.size;
    }
}

package com.example.absencemanagementapp.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.absencemanagementapp.R
import com.example.absencemanagementapp.activities.seance.SeanceActivity
import com.example.absencemanagementapp.models.Module
import com.example.absencemanagementapp.models.Seance

class SeanceAdapter(
    private val seances: List<Seance>, val context: Context, val module: Module
) : RecyclerView.Adapter<SeanceAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var seance_date_tv: TextView = itemView.findViewById(R.id.seance_date);
        val seance_absences_tv: TextView = itemView.findViewById(R.id.seance_absences);
        val seance_type_tv: TextView = itemView.findViewById(R.id.seance_type);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.seance_view_design, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return seances.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seance = seances[position]
        holder.seance_date_tv.text = seance.date
        holder.seance_absences_tv.text = seance.total_absences.toString()
        holder.seance_type_tv.text = seance.type

        //add animation to list
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(
                context, R.anim.recycler_view_anim
            )
        )

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SeanceActivity::class.java)
            intent.putExtra("seance_id", seance.id)
            intent.putExtra("url", seance.qrCodeUrl)
            intent.putExtra("module_id", seance.n_module)
            startActivity(context, intent, null)
        }
    }
}
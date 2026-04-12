package com.example.assignment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R

class StatCardAdapter(
    private val stats: List<Triple<String, String, String>>
) : RecyclerView.Adapter<StatCardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView = view.findViewById(R.id.tvStatIcon)
        val tvValue: TextView = view.findViewById(R.id.tvStatValue)
        val tvLabel: TextView = view.findViewById(R.id.tvStatLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stat_card, parent, false)
        // Set fixed width for horizontal scroll
        val params = view.layoutParams
        params.width = (parent.measuredWidth * 0.4).toInt()
        view.layoutParams = params
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (icon, value, label) = stats[position]
        holder.tvIcon.text = icon
        holder.tvValue.text = value
        holder.tvLabel.text = label
    }

    override fun getItemCount() = stats.size
}

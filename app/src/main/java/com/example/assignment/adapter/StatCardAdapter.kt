package com.example.assignment.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R
import com.example.assignment.service.MenuService

class StatCardAdapter(
    private val stats: List<MenuService.StatItem>,
    private val accentColor: Int
) : RecyclerView.Adapter<StatCardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView = view.findViewById(R.id.tvStatIcon)
        val tvLabel: TextView = view.findViewById(R.id.tvStatLabel)
        val tvValue: TextView = view.findViewById(R.id.tvStatValue)
        val vIconBg: View = view.findViewById(R.id.vIconBg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stat_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stat = stats[position]
        holder.tvIcon.text = stat.icon
        holder.tvLabel.text = stat.label
        holder.tvValue.text = stat.value
        holder.tvValue.setTextColor(accentColor)

        // Tint icon background circle with accent color at 20% opacity
        val iconBg = holder.vIconBg.background as? GradientDrawable
        iconBg?.setColor(Color.argb(50, Color.red(accentColor), Color.green(accentColor), Color.blue(accentColor)))

        // Set accent border matching menu cards
        val cardBg = holder.itemView.background as? GradientDrawable
        cardBg?.setStroke(1, accentColor)
    }

    override fun getItemCount() = stats.size
}

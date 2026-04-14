package com.example.assignment.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R
import com.example.assignment.model.MenuItem

class MenuCardAdapter(
    private val items: List<MenuItem>,
    private val onItemClick: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuCardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView = view.findViewById(R.id.tvMenuIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvMenuTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvIcon.text = item.icon
        holder.tvTitle.text = item.title

        // Set accent border
        val bg = holder.itemView.background as? GradientDrawable
        bg?.setStroke(1, item.accentColor)

        // Press animation
        holder.itemView.setOnClickListener {
            val scaleX = ObjectAnimator.ofFloat(it, "scaleX", 1f, 0.92f, 1f)
            val scaleY = ObjectAnimator.ofFloat(it, "scaleY", 1f, 0.92f, 1f)
            val set = AnimatorSet()
            set.playTogether(scaleX, scaleY)
            set.duration = 200
            set.start()

            it.postDelayed({ onItemClick(item) }, 200)
        }
    }

    override fun getItemCount() = items.size
}

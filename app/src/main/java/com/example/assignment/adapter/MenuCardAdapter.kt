package com.example.assignment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R
import com.example.assignment.model.MenuItem

class MenuCardAdapter(
    private val items: List<MenuItem>
) : RecyclerView.Adapter<MenuCardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView = view.findViewById(R.id.tvMenuIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvMenuTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvMenuDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvIcon.text = item.icon
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.description

        holder.itemView.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                holder.itemView.context.getString(R.string.coming_soon),
                Toast.LENGTH_SHORT
            ).show()
        }

        // Scale animation on press
        holder.itemView.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start()
                }
                android.view.MotionEvent.ACTION_UP,
                android.view.MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false
        }
    }

    override fun getItemCount() = items.size
}

package com.crecheconnect.crechemanagement_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParentListAdapter(
    private val parents: List<User>,
    private val onParentClick: (User) -> Unit
) : RecyclerView.Adapter<ParentListAdapter.ParentViewHolder>() {

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvParentName: TextView = itemView.findViewById(R.id.tvParentName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parent, parent, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParentViewHolder, position: Int) {
        val parent = parents[position]
        holder.tvParentName.text = parent.parentName.ifEmpty { parent.email }

        holder.itemView.setOnClickListener {
            onParentClick(parent)
        }
    }

    override fun getItemCount(): Int = parents.size
}

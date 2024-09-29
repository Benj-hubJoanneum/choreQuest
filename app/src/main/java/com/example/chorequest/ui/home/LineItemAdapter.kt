package com.example.chorequest.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chorequest.databinding.LineItemBinding

data class LineItem(val title: String, val date: String, val assignee: String)

class LineItemAdapter(private val items: List<LineItem>) :
    RecyclerView.Adapter<LineItemAdapter.LineItemViewHolder>() {

    class LineItemViewHolder(val binding: LineItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineItemViewHolder {
        val binding = LineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LineItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineItemViewHolder, position: Int) {
        val item = items[position]
        holder.binding.itemTitle.text = item.title
        holder.binding.itemDate.text = item.date
        holder.binding.itemAssignee.text = item.assignee
    }
    override fun getItemCount() = items.size
}

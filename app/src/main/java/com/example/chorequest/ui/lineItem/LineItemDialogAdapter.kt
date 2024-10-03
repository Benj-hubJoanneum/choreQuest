package com.example.chorequest.ui.lineItem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chorequest.databinding.LineItemBinding
import com.example.chorequest.model.LineItem

class LineItemDialogAdapter(
    private var items: List<LineItem>
) : RecyclerView.Adapter<LineItemDialogAdapter.LineItemViewHolder>() {

    class LineItemViewHolder(val binding: LineItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LineItemBinding.inflate(inflater, parent, false)
        return LineItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineItemViewHolder, position: Int) {
        val item = items[position]
        holder.binding.lineItem = item
        holder.binding.executePendingBindings()
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<LineItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}

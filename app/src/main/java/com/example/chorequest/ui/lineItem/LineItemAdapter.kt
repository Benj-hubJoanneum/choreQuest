package com.example.chorequest.ui.lineItem

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chorequest.databinding.LineItemBinding
import com.example.chorequest.model.LineItem

class LineItemAdapter(
    private var items: List<LineItem>,
    private val onItemClicked: (String) -> Unit // Click listener
) : RecyclerView.Adapter<LineItemAdapter.LineItemViewHolder>() {

    class LineItemViewHolder(val binding: LineItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineItemViewHolder {
        val binding = LineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LineItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineItemViewHolder, position: Int) {
        val item = items[position]
        holder.binding.lineItem = item
        holder.binding.executePendingBindings()

        // Handle item click
        holder.itemView.setOnClickListener {
            onItemClicked(item.uuid)
        }
    }


    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<LineItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        items = items.toMutableList().also {
            it.removeAt(position)
        }
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }

    fun addItem(position: Int, item: LineItem) {
        items = items.toMutableList().also {
            it.add(position, item)
        }
        notifyItemInserted(position)
    }
}
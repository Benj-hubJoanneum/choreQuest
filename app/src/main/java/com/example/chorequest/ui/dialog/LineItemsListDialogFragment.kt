package com.example.chorequest.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chorequest.databinding.FragmentDialogLineitemListBinding
import com.example.chorequest.model.LineItem
import com.example.chorequest.repositories.LineItemRepository
import com.example.chorequest.ui.Adapter.LineItemAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LineItemsListDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogLineitemListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LineItemAdapter
    private val repository = LineItemRepository()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDialogLineitemListBinding.inflate(layoutInflater)

        // Initialize RecyclerView
        adapter = LineItemAdapter(emptyList()) { uuid ->
            // No action needed for dialog as no item click handling here
        }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = adapter

        // Retrieve UUID passed from MyChoresFragment
        val uuid = arguments?.getString("uuid") ?: return super.onCreateDialog(savedInstanceState)

        // Fetch history based on the UUID in a coroutine
        CoroutineScope(Dispatchers.Main).launch {
            val history = repository.getLineItemHistoryById(uuid)
            if (history != null) {
                updateRecyclerView(history)
            }
        }

        binding.root.setOnClickListener {
            dismiss()
        }

        return Dialog(requireContext()).apply {
            setContentView(binding.root)
            setCancelable(true)
            window?.setGravity(Gravity.TOP)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    // Update the RecyclerView with the fetched history
    private fun updateRecyclerView(history: List<LineItem>) {
        adapter.updateItems(history)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        // Pass UUID to the dialog when creating an instance
        fun newInstance(uuid: String): LineItemsListDialogFragment {
            val dialog = LineItemsListDialogFragment()
            val args = Bundle().apply {
                putString("uuid", uuid)
            }
            dialog.arguments = args
            return dialog
        }
    }
}

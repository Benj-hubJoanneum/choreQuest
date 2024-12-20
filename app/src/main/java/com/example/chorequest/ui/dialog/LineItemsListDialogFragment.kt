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
import com.example.chorequest.ui.adapter.LineItemAdapter
import com.example.chorequest.service.FireStoreService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LineItemsListDialogFragment : DialogFragment() {

    private var _binding: FragmentDialogLineitemListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LineItemAdapter
    private val fireStoreService = FireStoreService() // Initialize Firestore service
    private val repository = LineItemRepository(fireStoreService)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentDialogLineitemListBinding.inflate(layoutInflater)

        // Initialize RecyclerView
        adapter = LineItemAdapter(emptyList()) { uuid ->
            // On click, show another dialog for the clicked LineItem's references
            val dialog = newInstance(uuid)
            dialog.show(parentFragmentManager, "NestedLineItemDialog")
        }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = adapter

        // Retrieve UUID passed from MyChoresFragment
        val uuid = arguments?.getString("uuid") ?: return super.onCreateDialog(savedInstanceState)

        // Fetch LineItem references based on the UUID
        CoroutineScope(Dispatchers.Main).launch {
            val lineItems = fetchReferencedLineItems(uuid)
            if (lineItems != null) {
                updateRecyclerView(lineItems)
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

    /**
     * Fetch referenced LineItems recursively based on the UUID.
     */
    private suspend fun fetchReferencedLineItems(uuid: String): List<LineItem>? {
        val lineItems =
            repository.getHistoryByLineItemID(uuid) ?: return null // Fetch the main LineItem

        // Return the main LineItem and its references as a combined list
        return lineItems
    }

    /**
     * Update the RecyclerView with the fetched LineItems.
     */
    private fun updateRecyclerView(lineItems: List<LineItem>) {
        adapter.updateItems(lineItems)
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

package com.example.chorequest.ui.myChores

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chorequest.databinding.FragmentMychoresBinding
import com.example.chorequest.repositories.LineItemRepository
import com.example.chorequest.ui.lineItem.LineItemDialogAdapter

class LineItemListDialogFragment : DialogFragment() {

    private var _binding: FragmentMychoresBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialogViewModel: LineItemDialogViewModel
    private lateinit var adapter: LineItemDialogAdapter

    private var lineItemId: String? = null

    companion object {
        private const val ARG_LINE_ITEM_UUID = "line_item_uuid"

        // newInstance method to create an instance with uuid
        fun newInstance(lineItemUuid: String): LineItemListDialogFragment {
            val fragment = LineItemListDialogFragment()
            val args = Bundle().apply {
                putString(ARG_LINE_ITEM_UUID, lineItemUuid)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lineItemId = arguments?.getString(ARG_LINE_ITEM_UUID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMychoresBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        val repository = LineItemRepository()
        dialogViewModel = ViewModelProvider(
            this,
            LineItemDialogViewModelFactory(repository)
        )[LineItemDialogViewModel::class.java]

        // Set up RecyclerView
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LineItemDialogAdapter(emptyList()) // Initially empty
        recyclerView.adapter = adapter

        // Observe LineItem history and update the RecyclerView
        dialogViewModel.lineItemHistory.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }

        // Fetch the LineItem history using the provided uuid
        lineItemId?.let { id ->
            dialogViewModel.fetchLineItemHistory(id)
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

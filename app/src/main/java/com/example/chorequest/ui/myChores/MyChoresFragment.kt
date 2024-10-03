package com.example.chorequest.ui.myChores

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chorequest.ui.dialog.LineItemsListDialogFragment
import com.example.chorequest.databinding.FragmentMychoresBinding
import com.example.chorequest.repositories.LineItemRepository
import com.example.chorequest.ui.Adapter.LineItemAdapter
import com.google.android.material.snackbar.Snackbar

class MyChoresFragment : Fragment() {

    private var _binding: FragmentMychoresBinding? = null
    private val binding get() = _binding!!

    private lateinit var myChoresViewModel: MyChoresViewModel
    private lateinit var adapter: LineItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMychoresBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        val repository = LineItemRepository()
        myChoresViewModel = ViewModelProvider(this, MyChoresViewModelFactory(repository))[MyChoresViewModel::class.java]

        // Set up RecyclerView for the list of chores (LineItemAdapter)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Handle item clicks, passing the uuid to the dialog
        adapter = LineItemAdapter(emptyList()) { uuid ->
            val dialog = LineItemsListDialogFragment.newInstance(uuid)
            dialog.show(parentFragmentManager, "SimpleDialogFragment")
        }
        recyclerView.adapter = adapter

        // Observe data from ViewModel
        myChoresViewModel.lineItems.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }

        // Fetch data from server
        myChoresViewModel.fetchLineItems()

        // Set up swipe-to-remove functionality
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                adapter.removeItem(position)

                // Show Snackbar with Undo option
                Snackbar.make(binding.recyclerView, "Item removed", Snackbar.LENGTH_LONG).show()
            }

        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

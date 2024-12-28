package com.example.chorequest.ui.addLineItem

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chorequest.databinding.FragmentAddlineitemBinding
import java.util.Calendar

class AddLineItemDialogFragment : Fragment() {

    private var _binding: FragmentAddlineitemBinding? = null
    private val binding get() = _binding!!

    private lateinit var addLineItemViewModel: AddLineItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddlineitemBinding.inflate(inflater, container, false)

        addLineItemViewModel = ViewModelProvider(this)[AddLineItemViewModel::class.java]

        binding.textDate.setOnClickListener {
            showDatePickerDialog()
        }

        binding.signInButton.setOnClickListener {
            val title = binding.textTitle.text.toString()
            val date = binding.textDate.text.toString()
            val assignee = binding.textAssignee.text.toString()

            if (validateInputs(title, date, assignee)) {
                addLineItemViewModel.addLineItem(title, date, assignee)
                Toast.makeText(requireContext(), "Item added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                binding.textDate.setText(date)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun validateInputs(title: String, date: String, assignee: String): Boolean {
        return title.isNotBlank() && date.isNotBlank() && assignee.isNotBlank()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

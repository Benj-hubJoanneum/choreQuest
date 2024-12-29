package com.example.chorequest.ui.addLineItem

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chorequest.R
import com.example.chorequest.databinding.FragmentAddlineitemBinding
import com.example.chorequest.model.Constant

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

        parentFragmentManager.setFragmentResultListener(Constant.CAMERA_RESULT, this) { _, bundle ->
            val imageUri = bundle.getString(Constant.IMAGE_URI)
            if (imageUri != null) {
                binding.imageItem.setImageURI(Uri.parse(imageUri))
            }
        }

        initializeUI()
        return binding.root
    }

    private fun initializeUI() {
        addLineItemViewModel = ViewModelProvider(this)[AddLineItemViewModel::class.java]

        binding.textDate.setOnClickListener { showDatePickerDialog() }

        binding.imageItem.setOnClickListener {
            findNavController().navigate(R.id.cameraFragment)
        }

        binding.signInButton.setOnClickListener {
            val title = binding.textTitle.text.toString()
            val date = binding.textDate.text.toString()
            val assignee = binding.textAssignee.text.toString()

            if (validateInputs(title, date, assignee)) {
                addLineItemViewModel.addLineItem(title, date, assignee)
                showToast("Item added successfully!")
            } else {
                showToast("Please fill all fields")
            }
        }
    }

    private fun validateInputs(title: String, date: String, assignee: String): Boolean {
        return title.isNotBlank() && date.isNotBlank() && assignee.isNotBlank()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedDay.${selectedMonth + 1}.$selectedYear"
            binding.textDate.setText(selectedDate)
        }, year, month, day).show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
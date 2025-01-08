package com.example.chorequest.ui.addLineItem

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chorequest.R
import com.example.chorequest.databinding.FragmentAddlineitemBinding
import com.example.chorequest.model.Constant
import com.example.chorequest.repositories.ImageRepository
import com.example.chorequest.repositories.LineItemRepository
import com.example.chorequest.service.FireStoreService
import com.example.chorequest.ui.modelFactory.ViewModelFactory

class AddLineItemFragment : Fragment() {

    private var _binding: FragmentAddlineitemBinding? = null
    private val binding get() = _binding!!

    private lateinit var addLineItemViewModel: AddLineItemViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddlineitemBinding.inflate(inflater, container, false)

        val fireStoreService = FireStoreService()
        val repository = LineItemRepository(fireStoreService)
        val imageRepository = ImageRepository()
        addLineItemViewModel = ViewModelProvider(
            this,
            ViewModelFactory(repository, imageRepository)
        )[AddLineItemViewModel::class.java]

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
        binding.textDate.setOnClickListener { showDatePickerDialog() }

        fetchAssigneeOptions()

        binding.imageItem.setOnClickListener {
            findNavController().navigate(R.id.cameraFragment)
        }

        binding.signInButton.setOnClickListener {
            val title = binding.textTitle.text.toString()
            val date = binding.textDate.text.toString()
            val assigneeId = selectedAssigneeId
            val image = binding.imageItem.drawToBitmap()

            if (validateInputs(title, date, assigneeId)) {
                addLineItemViewModel.addLineItem(title, date, assigneeId!!, "6CL3twvvJP0AoNvPMVoT", image)
                showToast("Item added successfully!")
            } else {
                showToast("Please fill all fields")
            }
        }
    }

    private var selectedAssigneeId: String? = null
    private fun fetchAssigneeOptions() {
        addLineItemViewModel.fetchGroupUsers("6CL3twvvJP0AoNvPMVoT") { userMap ->
            val names = userMap.values.toList()
            val ids = userMap.keys.toList()

            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                names
            )
            adapter.setDropDownViewResource(R.layout.spinner_item) // Custom layout for the dropdown
            binding.spinnerAssignee.adapter = adapter

            // Save the selected user ID when the spinner value changes
            binding.spinnerAssignee.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedAssigneeId = ids[position] // Get the corresponding user ID
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    selectedAssigneeId = null // Clear the selection
                }
            }
        }
    }


    private fun validateInputs(title: String, date: String, assigneeId: String?): Boolean {
        return title.isNotBlank() && date.isNotBlank()
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

    private fun observeViewModel() {
        addLineItemViewModel.addLineItemResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                showToast(it)
                findNavController().popBackStack()
            }
            result.onFailure {
                showToast("Failed to add item: ${it.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
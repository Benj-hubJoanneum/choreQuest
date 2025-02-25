package com.example.chorequest.ui.allChores

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chorequest.databinding.FragmentAllchoresBinding
import com.example.chorequest.repositories.ImageRepository
import com.example.chorequest.repositories.LineItemRepository
import com.example.chorequest.service.FireStoreService
import com.example.chorequest.ui.adapter.LineItemAdapter
import com.example.chorequest.ui.dialog.LineItemsListDialogFragment
import com.example.chorequest.ui.modelFactory.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class AllChoresFragment : Fragment() {

    private var _binding: FragmentAllchoresBinding? = null
    private val binding get() = _binding!!

    private lateinit var allChoresViewModel: AllChoresViewModel
    private lateinit var adapter: LineItemAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllchoresBinding.inflate(inflater, container, false)

        // Initialize ViewModel with FireStoreService
        val fireStoreService = FireStoreService()
        val repository = LineItemRepository(fireStoreService)
        val imageRepository = ImageRepository()
        allChoresViewModel = ViewModelProvider(
            this,
            ViewModelFactory(repository, imageRepository)
        )[AllChoresViewModel::class.java]

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
        allChoresViewModel.lineItems.observe(viewLifecycleOwner) { items ->
            adapter.updateItems(items)
        }

        // Fetch line items for a specific group (e.g., "6CL3twvvJP0AoNvPMVoT")
        val groupId = "6CL3twvvJP0AoNvPMVoT"
        allChoresViewModel.fetchLineItemsForGroup(groupId)

        // Swipe-to-remove setup
        setupSwipeToRemove()

        testImageUpload()

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
                adapter.removeItem(position)

                Snackbar.make(binding.recyclerView, "Item removed", Snackbar.LENGTH_LONG).show()
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun testImageUpload() {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888) // 100x100px white image
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, 100f, 100f, paint)

        val file = File(requireContext().cacheDir, "test_image.png")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()

        val mediaType = "image/png".toMediaTypeOrNull()
        val requestBody = file.asRequestBody(mediaType)
        val imagePart = okhttp3.MultipartBody.Part.createFormData("image", file.name, requestBody)

        allChoresViewModel.uploadImage(imagePart)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
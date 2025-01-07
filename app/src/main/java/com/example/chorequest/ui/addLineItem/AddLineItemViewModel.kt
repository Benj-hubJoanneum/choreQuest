package com.example.chorequest.ui.addLineItem

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chorequest.model.LineItem
import com.example.chorequest.repositories.ImageRepository
import com.example.chorequest.repositories.LineItemRepository
import kotlinx.coroutines.launch
import java.util.*

class AddLineItemViewModel(
    private val repository: LineItemRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _addLineItemResult = MutableLiveData<Result<String>>()

    fun addLineItem(title: String, date: String, assignee: String, groupID: String, image: Bitmap) {
        if (title.isBlank() || date.isBlank() || assignee.isBlank()) {
            _addLineItemResult.value = Result.failure(IllegalArgumentException("Title, date, or assignee cannot be blank"))
            return
        }

        viewModelScope.launch {
            try {
                val imageName = generateFileName(date)
                val imagePart = imageRepository.prepareImagePart(image, imageName)

                val response = imageRepository.uploadImage(imagePart)
                if (response.isSuccessful) {
                    val imageUrl = imageRepository.buildImageUri(imageName)
                    val lineItem = LineItem(
                        title = title,
                        date = date,
                        assignee = assignee,
                        imageUrl = imageUrl
                    )

                    repository.addLineItem(lineItem, groupID)
                    _addLineItemResult.value = Result.success("LineItem added successfully!")
                } else {
                    throw Exception("Image upload failed: HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                _addLineItemResult.value = Result.failure(e)
            }
        }
    }

    private fun generateFileName(date: String): String {
        val sanitizedDate = date.replace(" ", "_").replace("[^a-zA-Z0-9_\\-]".toRegex(), "")
        return "IMG_${UUID.randomUUID()}_$sanitizedDate.jpg"
    }
}

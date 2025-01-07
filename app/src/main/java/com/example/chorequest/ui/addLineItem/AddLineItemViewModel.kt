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

class AddLineItemViewModel(
    private val repository: LineItemRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    private val _addLineItemResult = MutableLiveData<Result<String>>()

    fun addLineItem(title: String, date: String, assignee: String, groupID: String, image: Bitmap) {
        viewModelScope.launch {
            try {
                val imagePart = imageRepository.prepareImagePart(image)

                val response = imageRepository.uploadImage(imagePart)
                if (response.isSuccessful) {
                    val uploadedImageUrl = response.headers()["Location"] ?: "Default URL"

                    val lineItem = LineItem(
                        title = title,
                        date = date,
                        assignee = assignee,
                        imageUrl = uploadedImageUrl
                    )

                    repository.addLineItem(lineItem, groupID)

                    _addLineItemResult.value = Result.success("LineItem added successfully!")
                } else {
                    throw Exception("Image upload failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                _addLineItemResult.value = Result.failure(e)
            }
        }
    }

}

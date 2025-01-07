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
                val imageName = "$6CL3twvvJP0AoNvPMVoT_$date.jpg".replace(" ", "_") // Replace spaces with underscores
                val imagePart = imageRepository.prepareImagePart(image, imageName)

                val response = imageRepository.uploadImage(imagePart)
                if (response.isSuccessful) {
                    val lineItem = LineItem(
                        title = title,
                        date = date,
                        assignee = assignee,
                        imageUrl = imageRepository.buildImageUri(imageName)
                    )

                    repository.addLineItem(lineItem, "6CL3twvvJP0AoNvPMVoT")

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

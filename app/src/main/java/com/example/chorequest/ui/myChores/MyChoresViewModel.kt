package com.example.chorequest.ui.myChores

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chorequest.model.LineItem
import com.example.chorequest.repositories.ImageRepository
import com.example.chorequest.repositories.LineItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Response

class MyChoresViewModel(
    private val repository: LineItemRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val _lineItems = MutableLiveData<List<LineItem>>()
    val lineItems: LiveData<List<LineItem>> get() = _lineItems

    fun fetchLineItems() {
        viewModelScope.launch {
            try {
                val items = repository.getLineItems()
                _lineItems.postValue(items ?: emptyList())

            } catch (e: Exception) {
                _lineItems.postValue(emptyList())
            }
        }
    }

    fun uploadImage(imagePart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                imageRepository.uploadImage(imagePart)
            } catch (e: Exception) {
            }
        }
    }
}

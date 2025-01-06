package com.example.chorequest.ui.allChores

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chorequest.model.LineItem
import com.example.chorequest.repositories.ImageRepository
import com.example.chorequest.repositories.LineItemRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AllChoresViewModel(
    private val repository: LineItemRepository,
    private val imageRepository: ImageRepository,
) : ViewModel() {

    private val _lineItems = MutableLiveData<List<LineItem>>()
    val lineItems: LiveData<List<LineItem>> get() = _lineItems

    fun fetchLineItemsForGroup(groupId: String, assignee: String? = null) {
        viewModelScope.launch {
            val items = repository.getLineItemsForGroup(groupId)

            if (items?.isNotEmpty() == true && assignee != null){
                _lineItems.postValue(items.filter { it.assignee == assignee } ?: emptyList())
            } else {
                _lineItems.postValue(items ?: emptyList())
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
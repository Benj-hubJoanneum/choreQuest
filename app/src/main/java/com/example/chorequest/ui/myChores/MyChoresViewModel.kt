package com.example.chorequest.ui.myChores

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chorequest.model.LineItem
import com.example.chorequest.repositories.ImageRepository
import com.example.chorequest.repositories.LineItemRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class MyChoresViewModel(
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

    fun markItemAsDone(lineItem: LineItem) {
        viewModelScope.launch {
            // Update Firestore with the new status
            repository.updateLineItemStatus(lineItem.copy(isDone = true))

            // Update the LiveData list
            val updatedList = _lineItems.value?.map {
                if (it.uuid == lineItem.uuid) it.copy(isDone = true) else it
            }
            _lineItems.postValue(updatedList ?: emptyList())
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

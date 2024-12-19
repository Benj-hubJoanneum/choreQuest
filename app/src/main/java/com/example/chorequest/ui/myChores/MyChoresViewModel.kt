package com.example.chorequest.ui.myChores

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chorequest.model.LineItem
import com.example.chorequest.repositories.LineItemRepository
import kotlinx.coroutines.launch

class MyChoresViewModel(private val repository: LineItemRepository) : ViewModel() {

    private val _lineItems = MutableLiveData<List<LineItem>>()
    val lineItems: LiveData<List<LineItem>> get() = _lineItems

    fun fetchLineItemsForGroup(groupId: String) {
        viewModelScope.launch {
            val items = repository.getLineItemsForGroup(groupId)
            _lineItems.postValue(items ?: emptyList())
        }
    }
}

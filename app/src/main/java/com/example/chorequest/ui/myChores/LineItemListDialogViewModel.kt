package com.example.chorequest.ui.myChores

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chorequest.model.LineItem
import com.example.chorequest.repositories.LineItemRepository
import kotlinx.coroutines.launch

class LineItemDialogViewModel(private val repository: LineItemRepository) : ViewModel() {

    private val _lineItemHistory = MutableLiveData<List<LineItem>>()
    val lineItemHistory: LiveData<List<LineItem>> get() = _lineItemHistory

    // Function to fetch the history of a LineItem based on uuid
    fun fetchLineItemHistory(uuid: String) {
        viewModelScope.launch {
            val historyItems = repository.getLineItemHistoryById(uuid)
            _lineItemHistory.postValue(historyItems ?: emptyList())
        }
    }
}

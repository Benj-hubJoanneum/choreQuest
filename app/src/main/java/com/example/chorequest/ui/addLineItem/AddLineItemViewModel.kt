package com.example.chorequest.ui.addLineItem

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddLineItemViewModel : ViewModel() {

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> get() = _text

    fun addLineItem(title: String, date: String, assignee: String) {
        // Handle logic for adding line item
        // E.g., Save to database or send to a server
        _text.value = title // Example of updating LiveData
    }
}

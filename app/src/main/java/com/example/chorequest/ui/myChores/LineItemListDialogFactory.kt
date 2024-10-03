package com.example.chorequest.ui.myChores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chorequest.repositories.LineItemRepository

class LineItemDialogViewModelFactory(private val repository: LineItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LineItemDialogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LineItemDialogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

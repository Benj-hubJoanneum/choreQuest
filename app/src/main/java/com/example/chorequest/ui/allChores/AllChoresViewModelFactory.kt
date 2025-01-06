package com.example.chorequest.ui.allChores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chorequest.repositories.ImageRepository
import com.example.chorequest.repositories.LineItemRepository

class AllChoresViewModelFactory(
    private val repository: LineItemRepository,
    private val imageRepository: ImageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllChoresViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AllChoresViewModel(repository, imageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
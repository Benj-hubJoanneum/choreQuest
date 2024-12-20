package com.example.chorequest.ui.myChores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chorequest.repositories.ImageRepository
import com.example.chorequest.repositories.LineItemRepository

class MyChoresViewModelFactory(
    private val repository: LineItemRepository,
    private val imageRepository: ImageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyChoresViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyChoresViewModel(repository, imageRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
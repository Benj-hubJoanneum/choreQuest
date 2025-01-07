package com.example.chorequest.ui.modelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.chorequest.repositories.ImageRepository
import com.example.chorequest.repositories.LineItemRepository
import com.example.chorequest.ui.addLineItem.AddLineItemViewModel
import com.example.chorequest.ui.allChores.AllChoresViewModel
import com.example.chorequest.ui.myChores.MyChoresViewModel
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: LineItemRepository,
    private val imageRepository: ImageRepository
) : ViewModelProvider.Factory {

    private val creators: Map<KClass<out ViewModel>, () -> ViewModel> = mapOf(
        AllChoresViewModel::class to { AllChoresViewModel(repository, imageRepository) },
        AddLineItemViewModel::class to { AddLineItemViewModel(repository, imageRepository) },
        MyChoresViewModel::class to { MyChoresViewModel(repository, imageRepository) }
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val creator = creators[modelClass.kotlin] ?: throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        return creator.invoke() as T
    }
}

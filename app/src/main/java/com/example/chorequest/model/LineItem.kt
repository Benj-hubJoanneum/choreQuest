package com.example.chorequest.model

data class LineItem(
    val uuid: String = "",
    val isDone: Boolean = false,
    val title: String = "",
    val date: String = "",
    val assignee: String = "",
    val lineItems: List<String> = listOf(),
    val imageUrl: String = "",
)
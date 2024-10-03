package com.example.chorequest.model

import java.util.UUID

data class LineItem(
    val uuid: String,
    val title: String,
    val date: String,
    val assignee: String,
    val lineItems: List<String> = listOf()
)
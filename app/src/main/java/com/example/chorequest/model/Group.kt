package com.example.chorequest.model

import java.util.UUID

data class Group(
    val uuid: String,
    val name: String,
    val lineItems: List<String> = listOf(),
    val users: List<String> = listOf()
)
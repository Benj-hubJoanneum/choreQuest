package com.example.chorequest.model

data class Group(
    val uuid: String,
    val name: String,
    val lineItems: List<String> = listOf(),
    val users: List<String> = listOf()
)
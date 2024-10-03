package com.example.chorequest.repositories

import com.example.chorequest.model.LineItem

class LineItemRepository {

    suspend fun getLineItems(): List<LineItem>? {
        return listOf(
            LineItem("1", "Orchideen gießen", "2024-10-02", "Anna"),
            LineItem("2", "Boden wischen", "2024-09-30", "Tom"),
            LineItem(
                "3",
                "Meet local King",
                "2024-10-01",
                "Hello. My name is Inigo Montoya. You killed my father. Prepare to die"
            )
        )
    }

    // New method to fetch history based on uuid
    suspend fun getLineItemHistoryById(uuid: String): List<LineItem>? {
        // Simulate fetching history by uuid
        return when (uuid) {
            "1" -> listOf(
                LineItem("1", "Orchideen gießen", "2024-09-02", "Anna"),
                LineItem("1", "Orchideen gießen", "2024-08-02", "Tom")
            )
            "2" -> listOf(
                LineItem("2", "Boden wischen", "2024-09-02", "Tom"),
                LineItem("2", "Boden wischen", "2024-08-02", "Anna")
            )
            else -> emptyList()
        }
    }
}

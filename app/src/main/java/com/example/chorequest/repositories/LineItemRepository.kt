package com.example.chorequest.repositories

import com.example.chorequest.model.LineItem

class LineItemRepository {

    suspend fun getLineItems(): List<LineItem>? {
        return listOf(
            LineItem("Orchideen gießen", "2024-10-02", "Anna"),
            LineItem("Boden wischen", "2024-09-30", "Tom"),
            LineItem(
                "Meet local King",
                "2024-10-01",
                "Hello. My name is Inigo Montoya. You killed my father. Prepare to die"
            )
        )
    }
}
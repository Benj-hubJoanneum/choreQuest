package com.example.chorequest.repositories

import com.example.chorequest.model.LineItem

class LineItemRepository {

    suspend fun getLineItems(): List<LineItem>? {
        return listOf(
            LineItem("1", "Orchideen gießen", "2024-10-02", "Anna", imageUrl = "https://yt3.googleusercontent.com/K8WVrQAQHTTwsHEtisMYcNai7p7XIlyEAdZg86qYw78ye57r5DRemHQ9Te4PcD_v98HB-ZvQjQ=s900-c-k-c0x00ffffff-no-rj"),
            LineItem("2", "Boden wischen", "2024-09-30", "Tom", imageUrl =  "https://yt3.googleusercontent.com/K8WVrQAQHTTwsHEtisMYcNai7p7XIlyEAdZg86qYw78ye57r5DRemHQ9Te4PcD_v98HB-ZvQjQ=s900-c-k-c0x00ffffff-no-rj"),
            LineItem(
                "3",
                "Meet local King",
                "2024-10-01",
                "Hello. My name is Inigo Montoya. You killed my father. Prepare to die",
                imageUrl = "https://yt3.googleusercontent.com/K8WVrQAQHTTwsHEtisMYcNai7p7XIlyEAdZg86qYw78ye57r5DRemHQ9Te4PcD_v98HB-ZvQjQ=s900-c-k-c0x00ffffff-no-rj"
            )
        )
    }

    // New method to fetch history based on uuid
    suspend fun getLineItemHistoryById(uuid: String): List<LineItem>? {
        // Simulate fetching history by uuid
        return when (uuid) {
            "1" -> listOf(
                LineItem("1", "Orchideen gießen", "2024-09-02", "Anna"),
                LineItem("1", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.", "2024-08-02", "Tom"),
                LineItem("1", "Orchideen gießen", "2024-07-02", "Anna"),
                LineItem("1", "Orchideen gießen", "2024-06-02", "Tom"),
                LineItem("1", "Orchideen gießen", "2024-05-02", "Anna"),
                LineItem("1", "Orchideen gießen", "2024-04-02", "Tom"),
                LineItem("1", "Orchideen gießen", "2024-03-02", "Anna"),
                LineItem("1", "Orchideen gießen", "2024-02-02", "Tom"),
                LineItem("1", "Orchideen gießen", "2024-01-02", "Anna"),
                LineItem("1", "Orchideen gießen", "2023-12-02", "Tom"),
                LineItem("1", "Orchideen gießen", "2023-11-02", "Anna"),
                LineItem("1", "Orchideen gießen", "2023-10-02", "Tom"),
                LineItem("1", "Orchideen gießen", "2023-09-02", "Anna"),
                LineItem("1", "Orchideen gießen", "2023-08-02", "Tom"),
                LineItem("1", "Orchideen gießen", "2023-07-02", "Anna"),
                LineItem("1", "Orchideen gießen", "2023-06-02", "Tom"),
            )
            "2" -> listOf(
                LineItem("2", "Boden wischen", "2023-09-02", "Tom"),
                LineItem("2", "Boden wischen", "2023-08-02", "Anna")
            )
            else -> emptyList()
        }
    }
}

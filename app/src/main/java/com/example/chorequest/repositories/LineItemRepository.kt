package com.example.chorequest.repositories

import com.example.chorequest.model.LineItem
import com.example.chorequest.model.User
import com.example.chorequest.service.FireStoreService

class LineItemRepository(private val fireStoreService: FireStoreService) {

    suspend fun getLineItemsForGroup(groupID: String): List<LineItem>? {
        return try {
            val group = fireStoreService.getGroupByID(groupID)
            group?.lineItems?.mapNotNull { lineItemID ->
                fireStoreService.getLineItemByID(lineItemID)?.let { lineItem ->
                    // Replace assignee UUID with user name
                    val userName = fireStoreService.getUsersByID(lineItem.assignee)?.name ?: "Unknown"
                    lineItem.copy(assignee = userName)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateLineItemStatus(lineItem: LineItem) {
        try {
            // Update Firestore document with the new status
            fireStoreService.updateLineItemStatus(lineItem.uuid, true)
            android.util.Log.d("LineItemRepository", "LineItem ${lineItem.uuid} marked as done.")
        } catch (e: Exception) {
            android.util.Log.e("LineItemRepository", "Error updating LineItem status: ${e.message}")
        }
    }

    suspend fun getHistoryByLineItemID(lineItemID: String): List<LineItem>? {
        return try {
            val lineItem = fireStoreService.getLineItemByID(lineItemID)
            lineItem?.lineItems?.mapNotNull { it ->
                fireStoreService.getLineItemByID(it)?.let { lineItem ->
                    // Replace assignee UUID with user name
                    val userName = fireStoreService.getUsersByID(lineItem.assignee)?.name ?: "Unknown"
                    lineItem.copy(assignee = userName)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addLineItem(lineItem: LineItem, groupID: String) {
        try {
            fireStoreService.addLineItem(lineItem, groupID)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun addUser(user: User) {
        try {
            fireStoreService.addUser(user)
        } catch (e: Exception) {
            throw  e
        }
    }
}

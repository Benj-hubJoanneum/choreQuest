package com.example.chorequest.service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.chorequest.model.Group
import com.example.chorequest.model.LineItem
import com.example.chorequest.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue  // Add this import
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FireStoreService {

    private val db = Firebase.firestore

    /*
    OBJECT TEMPLATE
    val user = hashMapOf(
        "first" to "Ada",
        "last" to "Lovelace",
        "born" to 1815
    )
    */

    suspend fun addGroup(group: Group) {
        try {
            val documentReference = db.collection("groups").add(group).await()
            db.collection("groups").document(documentReference.id)
                .update("uuid", documentReference.id).await()
            Log.d(TAG, "Group added with ID: ${documentReference.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Error adding group", e)
        }
    }

    suspend fun addUser(user: User) {
        db.collection("users").add(user).addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            // update related Data
            db.collection("users").document(documentReference.id).update("uuid", documentReference.id)
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
    }

    suspend fun addLineItem(lineItem: LineItem, groupId: String) {
        try {
            val documentReference = db.collection("lineItems").add(lineItem).await()
            db.collection("lineItems").document(documentReference.id)
                .update("uuid", documentReference.id).await()

            db.collection("groups").document(groupId)
                .update("lineItems", FieldValue.arrayUnion(documentReference.id)).await()

            Log.d(TAG, "Line item added with ID: ${documentReference.id}")
        } catch (e: Exception) {
            Log.w(TAG, "Error adding line item", e)
        }
    }

    suspend fun getUsers(): List<User> {
        return try {
            db.collection("users").get().await().documents.mapNotNull { it.toObject(User::class.java) }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting user documents", e)
            emptyList()
        }
    }

    suspend fun getGroupByID(groupId: String): Group? {
        return try {
            // Fetch by document ID
            db.collection("groups").document(groupId).get().await().toObject(Group::class.java)
        } catch (e: Exception) {
            Log.w(TAG, "Error getting group by ID: $groupId", e)
            null
        }
    }

    suspend fun getUsersByID(userID: String): User? {
        return try {
            db.collection("users").document(userID).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            Log.w(TAG, "Error getting user document for ID $userID", e)
            null
        }
    }


    suspend fun getLineItemByID(lineItemId: String): LineItem? {
        return try {
            // Fetch by document ID
            db.collection("lineItems").document(lineItemId).get().await().toObject(LineItem::class.java)
        } catch (e: Exception) {
            Log.w(TAG, "Error getting line item by ID: $lineItemId", e)
            null
        }
    }

    suspend fun getGroups(): List<Group> {
        return try {
            db.collection("groups").get().await().documents.mapNotNull { it.toObject(Group::class.java) }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting groups", e)
            emptyList()
        }
    }

    suspend fun getLineItems(): List<LineItem> {
        return try {
            db.collection("lineItems").get().await().documents.mapNotNull { it.toObject(LineItem::class.java) }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting line items", e)
            emptyList()
        }
    }

    suspend fun addUserToGroup(user: User, groupId: String) {
        db.collection("groups").document(groupId)
            .update("users", FieldValue.arrayUnion(user.uuid)).addOnSuccessListener {
                Log.d(TAG, "User added to group successfully")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error adding user to group", e)
            }
    }
}
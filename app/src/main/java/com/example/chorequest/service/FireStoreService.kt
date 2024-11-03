package com.example.chorequest.service

import android.content.ContentValues.TAG
import android.util.Log
import com.example.chorequest.model.Group
import com.example.chorequest.model.LineItem
import com.example.chorequest.model.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue  // Add this import
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
        db.collection("groups").add(group).addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
    }

    suspend fun addUser(user: User) {
        db.collection("users").add(user).addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
    }

    suspend fun addLineItem(lineItem: LineItem, groupId: String) {
        db.collection("lineItems").add(lineItem).addOnSuccessListener { documentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }

        // Add LineItem UUID reference to the Group's lineItems array
        db.collection("groups").document(groupId)
            .update("lineItems", FieldValue.arrayUnion(lineItem.uuid.toString())).await()
    }

    suspend fun getUsers(): Collection<User> {
        return try {
            db.collection("users").get().await().documents.mapNotNull { document ->
                document.toObject(User::class.java)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting user documents", e)
            emptyList()
        }
    }

    suspend fun getUsersByID(userID: String): User? {
        try {
            db.collection("users").document(userID).get().await().let { documentSnapshot ->
                if (documentSnapshot.exists())
                    return documentSnapshot.toObject(User::class.java) else null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error getting user document", e)
        }
        return null
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

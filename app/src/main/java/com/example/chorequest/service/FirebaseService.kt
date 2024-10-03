package com.example.chorequest.service

import com.example.chorequest.model.Group
import com.example.chorequest.model.LineItem
import com.example.chorequest.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue  // Add this import
import kotlinx.coroutines.tasks.await

class FirebaseService {

    private val db = FirebaseFirestore.getInstance()

    suspend fun addGroup(group: Group) {
        db.collection("groups").document(group.uuid.toString()).set(group).await()
    }

    suspend fun addUser(user: User) {
        db.collection("users").document(user.uuid.toString()).set(user).await()
    }

    suspend fun addLineItem(lineItem: LineItem, groupId: String) {
        db.collection("lineItems").document(lineItem.uuid.toString()).set(lineItem).await()

        // Add LineItem UUID reference to the Group's lineItems array
        db.collection("groups").document(groupId)
            .update("lineItems", FieldValue.arrayUnion(lineItem.uuid.toString())).await()
    }

    suspend fun addUserToGroup(user: User, groupId: String) {
        // Add User to Group
        db.collection("groups").document(groupId)
            .update("users", FieldValue.arrayUnion(user.uuid.toString())).await()

        // Optionally, update the user's document to reflect which groups they are in
        db.collection("users").document(user.uuid.toString())
            .update("groups", FieldValue.arrayUnion(groupId)).await()
    }
}

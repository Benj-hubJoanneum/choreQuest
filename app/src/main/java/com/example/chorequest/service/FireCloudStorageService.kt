package com.example.chorequest.service

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class FireCloudStorageService {

    private val storage = Firebase.storage
    private val storageRef = storage.reference

    // Function to demonstrate reference comparisons
    fun demonstrateReferenceComparisons() {
        // Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child("mountains.jpg")

        // Create a reference to 'images/mountains.jpg'
        val mountainImagesRef = storageRef.child("images/mountains.jpg")

        // While the file names are the same, the references point to different files
        println(mountainsRef.name == mountainImagesRef.name) // true
        println(mountainsRef.path == mountainImagesRef.path) // false
    }

    // Function to create various references
    fun createStorageReferences(): List<Any> {
        // Create a reference with an initial file path and name
        val pathReference = storageRef.child("images/stars.jpg")

        // Create a reference to a file from a Google Cloud Storage URI
        val gsReference = storage.getReferenceFromUrl("gs://bucket/images/stars.jpg")

        // Create a reference from an HTTPS URL
        // Note that in the URL, characters are URL escaped!
        val httpsReference = storage.getReferenceFromUrl(
            "https://firebasestorage.googleapis.com/b/bucket/o/images%20stars.jpg"
        )

        return listOf(pathReference, gsReference, httpsReference)
    }
}
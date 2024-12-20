package com.example.chorequest.service.authentication


class FirebaseAuthService {

    /*private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    *//**
     * Sign up a new user with email and password.
     *//*
    fun signUp(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Sign-up successful")
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    *//**
     * Sign in an existing user with email and password.
     *//*
    fun signIn(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Sign-in successful")
                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    *//**
     * Check if a user is already signed in.
     *//*
    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

    *//**
     * Get the current user's email.
     *//*
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    *//**
     * Sign out the current user.
     *//*
    fun signOut() {
        auth.signOut()
    }*/
}

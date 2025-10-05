package com.example.donowaste.data

import com.example.donowaste.models.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsersRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val users = db.collection("users")

    /**
     * Ensures a user document exists and returns the user's profile.
     * Creates a default profile if one doesn't exist.
     * @return The user's profile.
     * @throws IllegalStateException if the user is not signed in or the profile can't be parsed.
     */
    suspend fun ensureAndGetUserProfile(): UserProfile {
        val user = auth.currentUser ?: throw IllegalStateException("User not signed in")
        val docRef = users.document(user.uid)
        val snap = docRef.get().await()

        if (!snap.exists()) {
            val newProfile = UserProfile(
                displayName = user.displayName ?: (user.email?.substringBefore('@') ?: "New User"),
                email = user.email ?: "",
                photoUrl = user.photoUrl?.toString(),
                phone = user.phoneNumber,
                role = "both" // Always default to "both" to trigger role selection
            )
            // Set the document with the initial data
            docRef.set(newProfile).await()
            // Immediately update with server timestamps
            docRef.update(
                mapOf(
                    "createdAt" to FieldValue.serverTimestamp(),
                    "lastSeenAt" to FieldValue.serverTimestamp()
                )
            ).await()
            // Return the profile we just created
            return newProfile
        } else {
            // Document exists, update last seen time and return the parsed object
            docRef.update("lastSeenAt", FieldValue.serverTimestamp()).await()
            return snap.toObject(UserProfile::class.java)
                ?: throw IllegalStateException("Failed to parse user profile from Firestore.")
        }
    }

    /**
     * Updates the role for the currently signed-in user.
     */
    suspend fun updateUserRole(role: String) {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")
        users.document(uid).update("role", role).await()
    }
}

package com.example.donowaste.data

import android.net.Uri
import com.example.donowaste.models.Donation
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class DonationsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val donations = db.collection("donations")
    private val storage = Firebase.storage

    /** Create /donations/{autoId} (optionally upload photo first) */
    suspend fun createDonation(
        title: String,
        category: String,
        address: String? = null,
        notes: String? = null,
        tags: List<String>? = null,
        expiresAt: Timestamp? = null,
        imageUri: Uri? = null
    ): String {
        val uid = requireNotNull(auth.currentUser?.uid) { "Not signed in" }

        val docRef = donations.document()
        val id = docRef.id
        val photoPath = "donations/$id/main.jpg"

        if (imageUri != null) {
            storage.reference.child(photoPath).putFile(imageUri).await()
        }

        val donation = Donation(
            donorId = uid,
            title = title,
            category = category,
            status = "open",
            claimedBy = null,
            createdAt = null, // server-set next line
            claimedAt = null,
            expiresAt = expiresAt,
            photoPath = photoPath,
            location = null,  // set if you have a GeoPoint
            address = address,
            notes = notes,
            tags = tags
        )

        docRef.set(donation).await()
        docRef.update("createdAt", FieldValue.serverTimestamp()).await()
        return id
    }

    /** Claim an open donation */
    suspend fun claimDonation(id: String) {
        val uid = requireNotNull(auth.currentUser?.uid) { "Not signed in" }
        val ref = donations.document(id)
        db.runTransaction { tx ->
            val snap = tx.get(ref)
            val status = snap.getString("status")
            if (status != "open") throw IllegalStateException("Already $status")
            tx.update(ref, mapOf(
                "status" to "claimed",
                "claimedBy" to uid,
                "claimedAt" to FieldValue.serverTimestamp()
            ))
        }.await()
    }
}
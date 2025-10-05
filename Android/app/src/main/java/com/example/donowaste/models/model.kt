package com.example.donowaste.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class UserProfile(
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val phone: String? = null,
    val role: String = "both",
    val createdAt: Timestamp? = null,   // server-set
    val lastSeenAt: Timestamp? = null   // server-set
)

data class Donation(
    val donorId: String = "",
    val title: String = "",
    val category: String = "",
    val status: String = "open",        // open | claimed | picked_up
    val claimedBy: String? = null,
    val createdAt: Timestamp? = null,   // server-set
    val claimedAt: Timestamp? = null,
    val expiresAt: Timestamp? = null,
    val photoPath: String = "",
    val location: GeoPoint? = null,
    val address: String? = null,
    val notes: String? = null,
    val tags: List<String>? = null
)
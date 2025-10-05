// File: app/src/main/java/com/example/donowaste/data/Models.kt
package com.example.donowaste.data

// Represents a single item being donated
data class Item(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val quality: String, // e.g., "new", "good", "used", "poor"
)

// Represents a collection of items submitted by a donator
data class Package(
    val id: String,
    val items: List<Item>,
    val locationLink: String,
    val distance: Double, // in miles or km
    val status: String // e.g., "pending", "accepted"
)

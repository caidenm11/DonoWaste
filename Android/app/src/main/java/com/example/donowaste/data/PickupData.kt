package com.example.donowaste.data

// In a real app, this would likely be in a models package
data class PickupPackage(
    val id: String,
    val name: String,
    val distance: Float,
    val quality: String // "New", "Good", "Poor"
)

// Hardcoded data for the proof of concept
val samplePickups = listOf(
    PickupPackage("pkg1", "Winter Clothes", 2.1f, "Good"),
    PickupPackage("pkg2", "Children's Toys", 0.8f, "New"),
    PickupPackage("pkg3", "Canned Goods", 5.4f, "New"),
    PickupPackage("pkg4", "Used Office Chairs", 3.2f, "Poor"),
)

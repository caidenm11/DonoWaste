package com.example.donowaste.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.donowaste.models.UserProfile

@Composable
fun AppNavigation(userProfile: UserProfile) {
    // You can now use the userProfile object to make decisions in your navigation
    // For example, you could show different start destinations based on the user's role.
    Text(text = "Main App Content for ${userProfile.displayName}")
}

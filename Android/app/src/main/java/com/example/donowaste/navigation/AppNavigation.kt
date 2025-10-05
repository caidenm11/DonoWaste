package com.example.donowaste.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.donowaste.models.UserProfile
import com.example.donowaste.screens.donator.CreateItemScreen
import com.example.donowaste.screens.donator.CreatePackageScreen
import com.example.donowaste.screens.donator.DonatorHomeScreen
import com.example.donowaste.screens.donator.RecipientScreen

@Composable
fun AppNavigation(userProfile: UserProfile) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "donator_home") {
        composable("donator_home") {
            DonatorHomeScreen(navController = navController)
        }
        composable("create_package") {
            CreatePackageScreen(navController = navController)
        }
        composable("recipients") {
            RecipientScreen(navController = navController)
        }
        composable("create_item") { // New destination added
            CreateItemScreen(navController = navController)
        }
    }
}

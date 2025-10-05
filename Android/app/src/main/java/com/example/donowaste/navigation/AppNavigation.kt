package com.example.donowaste.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.donowaste.screens.CategoryScreen
import com.example.donowaste.screens.auth.LoginScreen
import com.example.donowaste.screens.donatee.DonateeHomeScreen
import com.example.donowaste.screens.donatee.DonationsScreen
import com.example.donowaste.screens.donatee.PackageScreen
import com.example.donowaste.screens.donator.CreateItemScreen
import com.example.donowaste.screens.donator.CreatePackageScreen
import com.example.donowaste.screens.donator.DonatorHomeScreen
import com.example.donowaste.screens.donator.RecipientScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "category_page") {
        // Main and Auth
        composable("category_page") { CategoryScreen(navController) }
        composable("login_page/{userType}") { backStackEntry ->
            LoginScreen(
                navController = navController,
                userType = backStackEntry.arguments?.getString("userType")
            )
        }

        // Donatee Flow
        composable("donatee_home") { DonateeHomeScreen(navController) }
        composable("donations_page") { DonationsScreen() }
        composable("package_page/{packageId}") { backStackEntry ->
            PackageScreen(
//                navController = navController,
//                packageId = backStackEntry.arguments?.getString("packageId")
            )
        }

        // Donator Flow
        composable("donator_home") { DonatorHomeScreen() }
        composable("recipient_page") { RecipientScreen() }
        composable("create_package_page") { CreatePackageScreen() }
        composable("create_item_page") { CreateItemScreen() }
    }
}
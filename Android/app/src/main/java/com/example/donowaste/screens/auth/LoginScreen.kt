// File: app/src/main/java/com/example/donowaste/screens/auth/LoginScreen.kt
package com.example.donowaste.screens.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController, userType: String?) {
    // TODO: Build the Login and Sign-up UI here.
    // Use the `userType` ("donatee" or "donator") to customize the screen.
    Text(text = "Login screen for: ${userType.orEmpty()}")
    // On successful login, navigate to the correct home screen:
    // if (userType == "donatee") navController.navigate("donatee_home")
    // else navController.navigate("donator_home")
}

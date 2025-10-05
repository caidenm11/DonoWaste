package com.example.donowaste.screens.donator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun DonatorHomeScreen(navController: NavController) {
    // TODO: Add "Donations" and "Pickup" buttons
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.navigate("create_package")}) {
            Text(text = "Donate")
        }
        Button(onClick = { navController.navigate("recipients")}) {
            Text(text = "Recipients")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DonatorHomeScreen(navController = rememberNavController())
}

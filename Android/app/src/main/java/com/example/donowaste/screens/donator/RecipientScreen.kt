package com.example.donowaste.screens.donator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// Dummy data for previewing the list
data class DonatedPackage(val id: String, val contents: String, val recipient: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipientScreen(navController: NavController) {
    // In a real app, you would fetch this data from a repository
    val donatedPackages = remember {
        listOf(
            DonatedPackage("pkg1", "Package of winter clothes", "Goodwill"),
            DonatedPackage("pkg2", "Box of assorted toys", "Children's Hospital"),
            DonatedPackage("pkg3", "Canned goods and non-perishables", "Local Food Bank")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Donations") })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (donatedPackages.isEmpty()) {
                item {
                    Text("You have not donated any packages yet.")
                }
            } else {
                items(donatedPackages) { pkg ->
                    DonatedPackageCard(pkg)
                }
            }
        }
    }
}

@Composable
fun DonatedPackageCard(pkg: DonatedPackage) {
    Card(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Package: ${pkg.contents}",
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "Received by: ${pkg.recipient}",
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipientScreenPreview() {
    RecipientScreen(navController = rememberNavController())
}

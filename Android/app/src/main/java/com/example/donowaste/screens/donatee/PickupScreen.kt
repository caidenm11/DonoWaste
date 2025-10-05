package com.example.donowaste.screens.donatee

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.donowaste.data.PickupPackage
import com.example.donowaste.data.samplePickups

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupScreen(navController: NavController) {
    var sortBy by remember { mutableStateOf("Distance") }

    val qualityOrder = mapOf("New" to 0, "Good" to 1, "Poor" to 2)

    val sortedPickups = remember(sortBy) {
        when (sortBy) {
            "Distance" -> samplePickups.sortedBy { it.distance }
            "Ranking" -> samplePickups.sortedBy { qualityOrder[it.quality] }
            else -> samplePickups
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Available for Pickup") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Sort by:", modifier = Modifier.padding(end = 8.dp))
                Button(onClick = { sortBy = "Distance" }) {
                    Text("Distance")
                }
                Button(onClick = { sortBy = "Ranking" }) {
                    Text("Ranking")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(sortedPickups, key = { it.id }) { pkg ->
                    PickupCard(packageItem = pkg) {
                        navController.navigate("package_page/${pkg.id}")
                    }
                }
            }
        }
    }
}

@Composable
fun PickupCard(packageItem: PickupPackage, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(packageItem.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Distance: ${packageItem.distance} miles")
            Text("Quality: ${packageItem.quality}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PickupScreenPreview() {
    PickupScreen(navController = rememberNavController())
}

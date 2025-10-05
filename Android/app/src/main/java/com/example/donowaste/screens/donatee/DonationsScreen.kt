package com.example.donowaste.screens.donatee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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

// Data class for the donation item
data class Donation(
    val id: String,
    val packageName: String,
    val distance: String,
    val quality: String,
    val category: String
)

// Hardcoded data for the proof of concept
val sampleDonations = listOf(
    Donation("1", "Winter Clothes Bundle", "2.5 miles", "Good", "Clothing"),
    Donation("2", "Assorted Children's Toys", "5.1 miles", "New", "Toys"),
    Donation("3", "Canned Goods Collection", "1.2 miles", "New", "Food"),
    Donation("4", "Kitchenware Set", "7.8 miles", "Used", "Items"),
    Donation("5", "Summer T-Shirts", "3.0 miles", "Good", "Clothing"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationsScreen() {
    val filters = listOf("All", "Items", "Clothing", "Toys", "Food")
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredDonations = remember(selectedFilter) {
        if (selectedFilter == "All") {
            sampleDonations
        } else {
            sampleDonations.filter { it.category == selectedFilter }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Donations") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Filter bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    Button(
                        onClick = { selectedFilter = filter },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedFilter == filter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            contentColor = if (selectedFilter == filter) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(filter)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // List of donations
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filteredDonations, key = { it.id }) { donation ->
                    DonationCard(donation = donation, onAccept = {}, onDecline = {})
                }
            }
        }
    }
}

@Composable
fun DonationCard(
    donation: Donation,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(donation.packageName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Distance: ${donation.distance}")
            Text("Quality: ${donation.quality}")
            Text("Category: ${donation.category}")
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onDecline, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Decline")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onAccept) {
                    Text("Accept")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DonationsScreenPreview() {
    DonationsScreen()
}

package com.example.donowaste.screens.donatee

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.donowaste.data.samplePickups

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackageScreen(packageId: String?) {
    val packageItem = samplePickups.find { it.id == packageId }
    // Hardcoded address for the proof of concept
    val address = "123 Main Street, Vancouver, BC"

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(packageItem?.name ?: "Package Details") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (packageItem != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(packageItem.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Quality: ${packageItem.quality}", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Pickup Address:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(address, fontSize = 16.sp)
                    }
                }
            } else {
                Text("Package not found.")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PackageScreenPreview() {
    PackageScreen(packageId = "pkg1")
}

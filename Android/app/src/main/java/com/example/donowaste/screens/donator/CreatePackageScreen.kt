package com.example.donowaste.screens.donator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePackageScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create Donation Package") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_item") }) {
                Icon(Icons.Default.Add, contentDescription = "Add item")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Items in your package:")
            // This will be replaced with a list of items
            LazyColumn(modifier = Modifier.weight(1f)) {
                // For now, it's empty. We'll add items here later.
                item {
                    Text("Your package is empty. Add items using the '+' button.")
                }
            }
            Button(
                onClick = { /* TODO: Implement package submission logic */ },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Submit Package")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePackageScreenPreview() {
    CreatePackageScreen(navController = rememberNavController())
}

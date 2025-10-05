package com.example.donowaste.screens.donator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePackageScreen(navController: NavController) {
    var location by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Create a Package") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // This would typically list the items added to the package
            LazyColumn(modifier = Modifier.weight(1f)) {
                // Placeholder for item list
                item { Text("Items in this package will appear here.") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Add pickup location") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("create_item_page") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add an Item")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    // TODO: Finalize package and send to recipients
                    navController.popBackStack() // Go back to the main donor screen
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send to Recipients")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePackageScreenPreview() {
    CreatePackageScreen(navController = rememberNavController())
}

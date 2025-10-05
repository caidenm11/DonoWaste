package com.example.donowaste.screens.donator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateItemScreen(navController: NavController) {
    var isBrandNew by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add an Item") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // In a real app, this would open the camera or gallery
            Button(onClick = { /* TODO: Implement image selection */ }) {
                Text("Submit Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Is this item brand new?")
                Switch(
                    checked = isBrandNew,
                    onCheckedChange = { isBrandNew = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location selection buttons
            Row {
                Button(onClick = { /* TODO: Implement location search */ }) {
                    Text("Search for Location")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { /* TODO: Implement current location */ }) {
                    Text("Use Current Location")
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Pushes the button to the bottom

            Button(
                onClick = {
                    // TODO: Add item to the package list
                    navController.popBackStack() // Go back to the package screen
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Item to Package")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateItemScreenPreview() {
    CreateItemScreen(navController = rememberNavController())
}

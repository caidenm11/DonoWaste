package com.example.donowaste.screens.donator

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.donowaste.viewmodels.GeminiUiState
import com.example.donowaste.viewmodels.GeminiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateItemScreen(navController: NavController, geminiViewModel: GeminiViewModel = viewModel()) {
    var isBrandNew by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val geminiState by geminiViewModel.uiState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
            uri?.let {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(source)
                }
                geminiViewModel.analyzeImage(bitmap)
            }
        }
    )

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
            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Select Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            imageUri?.let {
                val bitmap = remember {
                    if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, it)
                        ImageDecoder.decodeBitmap(source)
                    }
                }
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = geminiState) {
                is GeminiUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is GeminiUiState.Success -> {
                    Text(state.output)
                }
                is GeminiUiState.Error -> {
                    Text(state.errorMessage, color = androidx.compose.ui.graphics.Color.Red)
                }
                else -> {}
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

            Spacer(modifier = Modifier.weight(1f))

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

// File: app/src/main/java/com/example/donowaste/MainActivity.kt
package com.example.donowaste

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.donowaste.navigation.AppNavigation
import com.example.donowaste.ui.theme.DonoWasteTheme // Make sure this import matches your theme file

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // DonoWasteTheme is usually generated automatically in the ui.theme package
            DonoWasteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // This is now the entry point of your UI
                    AppNavigation()
                }
            }
        }
    }
}

package com.example.donowaste.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF00C853), // Example Green
    secondary = androidx.compose.ui.graphics.Color(0xFFFFC107), // Example Amber
    tertiary = androidx.compose.ui.graphics.Color(0xFF64B5F6) // Example Blue
)

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF00C853),
    secondary = androidx.compose.ui.graphics.Color(0xFFFFC107),
    tertiary = androidx.compose.ui.graphics.Color(0xFF64B5F6)
)

@Composable
fun DonoWasteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // This will now correctly resolve to the one in Type.kt
        content = content
    )
}

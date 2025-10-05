package com.example.donowaste.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.donowaste.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeminiViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<GeminiUiState> = MutableStateFlow(GeminiUiState.Initial)
    val uiState: StateFlow<GeminiUiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro-vision",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun analyzeImage(image: Bitmap) {
        _uiState.value = GeminiUiState.Loading
        viewModelScope.launch {
            try {
                val inputContent = content {
                    image(image)
                    text("Is this image of good quality? Be very strict and critical. If it's blurry, poorly lit, or doesn't clearly show the item, it's bad quality.")
                }

                val response = generativeModel.generateContent(inputContent)
                _uiState.value = GeminiUiState.Success(response.text ?: "")
            } catch (e: Exception) {
                _uiState.value = GeminiUiState.Error(e.localizedMessage ?: "An error occurred")
            }
        }
    }
}

sealed interface GeminiUiState {
    object Initial : GeminiUiState
    object Loading : GeminiUiState
    data class Success(val output: String) : GeminiUiState
    data class Error(val errorMessage: String) : GeminiUiState
}

package com.example.ahorcado

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    var score by mutableStateOf(0)
        private set

    fun addScore() {
        score++
    }
}
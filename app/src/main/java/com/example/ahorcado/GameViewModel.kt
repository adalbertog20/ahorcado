package com.example.ahorcado

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    var score by mutableStateOf(0)
        private set
    var lives by mutableStateOf(3)
        private set

    var showDialog by mutableStateOf(false)
        private set

    fun addScore() {
        score++
    }

    fun setShowDialog() {
        showDialog = true
    }

    fun setShowDialogFalse() {
        showDialog = false
    }

    fun subLives() {
        lives--
    }
    fun resetGame() {
        lives = 3
        showDialog = false
    }
}
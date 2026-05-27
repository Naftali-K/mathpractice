package com.nk.mathpractice.models

data class HistoryItem(
    val expression: String,
    val userAnswer: String,
    val correctAnswer: Int,
    val isCorrect: Boolean
)
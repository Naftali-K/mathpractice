package com.nk.mathpractice.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nk.mathpractice.models.HistoryItem
import com.nk.mathpractice.models.Problem

class MainActivityViewModel : ViewModel() {

    private val _currentProblem = MutableLiveData<Problem?>()
    val currentProblem: LiveData<Problem?> = _currentProblem

    private val _history = MutableLiveData<List<HistoryItem>>(emptyList())
    val history: LiveData<List<HistoryItem>> = _history

    private val _score = MutableLiveData(Pair(0, 0)) // Correct, Incorrect
    val score: LiveData<Pair<Int, Int>> = _score

    private val _showFeedback =
        MutableLiveData<Boolean?>() // true = correct, false = incorrect, null = none
    val showFeedback: LiveData<Boolean?> = _showFeedback

    private var selectedOperations = listOf<String>()
    private var digitCount = 1
    private var allowNegative = false

    fun setSettings(operations: List<String>, digits: Int, allowNegative: Boolean) {
        this.selectedOperations = operations
        this.digitCount = digits
        this.allowNegative = allowNegative
        generateNewProblem()
    }

    fun reset() {
        _history.value = emptyList()
        _score.value = Pair(0, 0)
        _showFeedback.value = null
        selectedOperations = emptyList()
        digitCount = 1
        allowNegative = false
        _currentProblem.value = null
    }

    fun generateNewProblem() {
        if (selectedOperations.isEmpty()) return

        val operation = selectedOperations.random()
        val range = when (digitCount) {
            1 -> 0..9
            2 -> 10..99
            else -> 100..999
        }

        var op1 = range.random()
        var op2 = range.random()

        // Apply logic for non-negative results if requested (mainly for subtraction)
        if (!allowNegative && operation == "-") {
            if (op1 < op2) {
                val temp = op1
                op1 = op2
                op2 = temp
            }
        }

        if (operation == "/" || operation == "%") {
            if (op2 == 0) op2 = 1
            if (operation == "/") {
                val product = op1 * op2
                op1 = product
            }
        }

        val result = when (operation) {
            "+" -> op1 + op2
            "-" -> op1 - op2
            "*" -> op1 * op2
            "/" -> op1 / op2
            "%" -> op1 % op2
            else -> 0
        }

        _currentProblem.value = Problem(op1, op2, operation, result)
        _showFeedback.value = null
    }

    fun checkAnswer(answerText: String) {
        val problem = _currentProblem.value ?: return
        val userAnswer = answerText.toIntOrNull()
        val isCorrect = userAnswer == problem.result

        val currentScore = _score.value ?: Pair(0, 0)
        if (isCorrect) {
            _score.value = currentScore.copy(first = currentScore.first + 1)
        } else {
            _score.value = currentScore.copy(second = currentScore.second + 1)
        }

        val historyItem = HistoryItem(
            expression = "${problem.operand1} ${problem.operation} ${problem.operand2}",
            userAnswer = answerText.ifEmpty { "?" },
            correctAnswer = problem.result,
            isCorrect = isCorrect
        )

        val currentHistory = _history.value?.toMutableList() ?: mutableListOf()
        currentHistory.add(0, historyItem)
        _history.value = currentHistory

        _showFeedback.value = isCorrect
    }
}
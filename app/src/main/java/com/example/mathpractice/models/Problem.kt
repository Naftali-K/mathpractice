package com.example.mathpractice.models

data class Problem(
    val operand1: Int,
    val operand2: Int,
    val operation: String,
    val result: Int
) {
    override fun toString(): String = "$operand1 $operation $operand2 = ?"
}
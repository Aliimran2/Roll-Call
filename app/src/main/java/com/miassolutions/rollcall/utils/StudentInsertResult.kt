package com.miassolutions.rollcall.utils

sealed class StudentInsertResult {
    data object Success : StudentInsertResult()
    data class Failure(val reason: String) : StudentInsertResult()
}
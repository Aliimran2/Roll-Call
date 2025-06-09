package com.miassolutions.rollcall.utils

sealed class StudentInsertResult {

    data object Success : StudentInsertResult()
    data object Duplicate : StudentInsertResult()
    data class Error(val message : String) : StudentInsertResult()
}
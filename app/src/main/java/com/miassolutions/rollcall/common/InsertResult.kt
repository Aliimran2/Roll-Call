package com.miassolutions.rollcall.common

sealed class InsertResult {
    data object Success : InsertResult()
    data class Failure(val reason : String) : InsertResult()
}
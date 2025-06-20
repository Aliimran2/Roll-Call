package com.miassolutions.rollcall.utils

sealed class StudentResult<out T> {
    data class Success<out T>(val data: T) : StudentResult<T>()
    data class Error(val message: String) : StudentResult<Nothing>()
    data object Loading : StudentResult<Nothing>()
}
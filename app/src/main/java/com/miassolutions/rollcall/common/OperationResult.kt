package com.miassolutions.rollcall.common


sealed class OperationResult<out T> {
    data object Loading : OperationResult<Nothing>()
    data class Success<out T>(val data: T) : OperationResult<T>()
    data class Error(val message: String) : OperationResult<Nothing>()

}
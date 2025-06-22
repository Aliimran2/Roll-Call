package com.miassolutions.rollcall.common

sealed class InsertClassResult<out T> {
    data class Success<out T>(val classData: T) : InsertClassResult<T>()
    data class Failure(val message: String, val error: String) : InsertClassResult<Nothing>()
}
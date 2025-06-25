package com.miassolutions.rollcall.ui.common

import com.miassolutions.rollcall.data.entities.StudentEntity

sealed class StudentUiState {
    data object Empty : StudentUiState()
    data class Success(val students: List<StudentEntity>) : StudentUiState()
    data class Failure(val message: String) : StudentUiState()
    data object Loading : StudentUiState()

}
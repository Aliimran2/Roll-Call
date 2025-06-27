package com.miassolutions.rollcall.ui.screens.studentlistscreen

import com.miassolutions.rollcall.data.entities.StudentEntity

sealed class StudentListUiState {
    data object Loading : StudentListUiState()
    data object Empty : StudentListUiState()
    data class Success(val studentList: List<StudentEntity>) : StudentListUiState() {
        val totalStudents: Int
            get() = studentList.size
    }
}


sealed class StudentListUiEvent {
    data class ShowSnackbar(val message : String)
    data object NavToAddUpdate : StudentListUiEvent()
    data object NavToDetail : StudentListUiEvent()
    data object NavToBack : StudentListUiEvent()
}
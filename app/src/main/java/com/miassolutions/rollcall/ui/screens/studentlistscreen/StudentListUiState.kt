package com.miassolutions.rollcall.ui.screens.studentlistscreen

import androidx.room.Query
import com.miassolutions.rollcall.data.entities.StudentEntity

data class StudentListUiState(
    val studentList: List<StudentEntity> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val totalCount: Int = 0,
    val filterCount : Int = 0
)


sealed class StudentListUiEvent {
    data class ShowSnackbar(val message: String) : StudentListUiEvent()
    data class NavigateToStudentDetail(val studentId: String, val studentName: String) :
        StudentListUiEvent()


    data class NavigateToAddOrEdit(
        val studentId: String?,
        val classId: String,
        val className: String,
    ) : StudentListUiEvent()

    data class DialPhone(val phone: String) : StudentListUiEvent()
    data class ShowDeleteConfirmation(val studentId: String) : StudentListUiEvent()
}
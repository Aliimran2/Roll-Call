package com.miassolutions.rollcall.ui.screens.classform

import java.util.Date

data class ClassFormUiState(
    val classId: String? = null,
    val className: String = "",
    val sectionName: String = "",
    val startDateStr: String = "",
    val endDateStr: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val teacherName: String = "",
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
)

sealed class ClassFormUiEvent {
    data class ShowToast(val message: String) : ClassFormUiEvent()
    data object NavigateBack : ClassFormUiEvent()
}

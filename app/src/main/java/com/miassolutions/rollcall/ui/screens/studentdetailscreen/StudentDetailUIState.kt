package com.miassolutions.rollcall.ui.screens.studentdetailscreen

data class StudentDetailUIState(
    val primaryProfile: PrimaryProfile? = null,
    val secondaryProfile: SecondaryProfile? = null,
    val attendanceStats: AttendanceStats? = null,
    val isLoading: Boolean? = null,
    val error: String? = null,
)


data class PrimaryProfile(

    val name: String,
    val imageUri: String,
    val rollNum: String,
    val regNum: String,
    val dateOfBirth: String,
    val dateOfAdmission: String,
    val bForm: String,
)

data class SecondaryProfile(
    val fatherName: String,
    val phoneNumber: String,
    val address: String,
)

data class AttendanceStats(
    val totalDays: Int,
    val presentDays: Int,
    val percentage: Float,
)


sealed class StudentDetailUiEvent {
    data object NavigateBack : StudentDetailUiEvent()
    data class ShowSnackbar(val message: String) : StudentDetailUiEvent()
}
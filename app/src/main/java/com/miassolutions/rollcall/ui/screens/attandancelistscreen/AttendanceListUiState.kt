package com.miassolutions.rollcall.ui.screens.attandancelistscreen

data class AttendanceListUiState(
    val isLoading: Boolean = false,
    val attendanceStats: List<AttendanceStatsItem> = emptyList(),
    val errorMessage: String? = null,
)


data class AttendanceStatsItem(
    val date: Long,
    val presentCount: Int,
    val totalCount: Int,
    val percentage: Int,
)


sealed class AttendanceStatsUiEvent {
    data class ShowSnackbar(val message: String) : AttendanceStatsUiEvent()
    data class NavToAddEditAttendance(val date: Long) : AttendanceStatsUiEvent()
    data class NavToReportAttendance(val date: Long) : AttendanceStatsUiEvent()

}
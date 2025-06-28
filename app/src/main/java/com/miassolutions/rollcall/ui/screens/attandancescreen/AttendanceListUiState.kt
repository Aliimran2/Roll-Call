package com.miassolutions.rollcall.ui.screens.attandancescreen

import com.miassolutions.rollcall.data.entities.AttendanceEntity

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
    data class NavToAddEditAttendance(val attendanceId: String) : AttendanceStatsUiEvent()
    data class NavToReportAttendance(val attendanceId: String) : AttendanceStatsUiEvent()
    data class ShowDeleteConfirmation(val attendanceId: String) : AttendanceStatsUiEvent()
}
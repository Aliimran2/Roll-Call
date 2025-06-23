package com.miassolutions.rollcall.ui.attendance

import com.miassolutions.rollcall.common.AttendanceFilter
import com.miassolutions.rollcall.common.AttendanceStatus

sealed class AttendanceUiEvent {
    data class SetDate(val date: Long) : AttendanceUiEvent()
    data class UpdateStatus(val studentId: String, val newStatus: AttendanceStatus) :
        AttendanceUiEvent()

    data class UpdateFilter(val filter: AttendanceFilter) : AttendanceUiEvent()
    data class UpdateSearchQuery(val query: String) : AttendanceUiEvent()
    data object SaveAttendance : AttendanceUiEvent()
}
package com.miassolutions.rollcall.ui.attendance

import com.miassolutions.rollcall.common.AttendanceFilter
import com.miassolutions.rollcall.common.AttendanceStatus

data class AttendanceUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val selectedDate: Long? = null,
    val filter: AttendanceFilter = AttendanceFilter.ALL,
    val searchQuery: String = "",
    val allStudents: List<StudentAttendance> = emptyList(),
    val counts: AttendanceCounts = AttendanceCounts(),
) {
    data class StudentAttendance(
        val classId : String,
        val studentId: String,
        val name: String,
        val rollNumber: String,
        val status: AttendanceStatus,
    )

    data class AttendanceCounts(
        val total: Int = 0,
        val present: Int = 0,
        val absent: Int = 0,
    )
}

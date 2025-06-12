package com.miassolutions.rollcall.ui.model

import com.miassolutions.rollcall.utils.AttendanceStatus

data class AttendanceUIModel(
    val studentId: String,
    val studentName: String,
    val rollNumber: Int,
    var attendanceStatus: AttendanceStatus = AttendanceStatus.PRESENT
)


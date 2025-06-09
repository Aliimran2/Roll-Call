package com.miassolutions.rollcall.data.entities

import com.miassolutions.rollcall.utils.AttendanceStatus

data class StudentWithAttendance(
    val rollNum : Int,
    val studentName : String,
    var attendanceStatus: AttendanceStatus
)

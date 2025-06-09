package com.miassolutions.rollcall.data.entities

import com.miassolutions.rollcall.utils.AttendanceStatus
import java.util.UUID

data class Attendance(
    val id : String = UUID.randomUUID().toString(),
    val studentId : String,
    val date : String,
    val attendanceStatus : AttendanceStatus
)

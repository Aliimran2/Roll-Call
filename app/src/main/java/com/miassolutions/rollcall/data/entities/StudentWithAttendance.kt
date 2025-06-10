package com.miassolutions.rollcall.data.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.miassolutions.rollcall.utils.AttendanceStatus

data class StudentWithAttendance(
   @Embedded val student: Student,
    @Relation(
        parentColumn = "studentId",
        entityColumn = "studentId"
    )
    val attendance: List<Attendance>
)

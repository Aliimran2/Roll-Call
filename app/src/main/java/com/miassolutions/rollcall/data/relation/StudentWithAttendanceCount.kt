package com.miassolutions.rollcall.data.relation

import androidx.room.Embedded
import com.miassolutions.rollcall.data.entities.StudentEntity

data class StudentWithAttendanceCount (
    @Embedded
    val student : StudentEntity,
    val presentCount : Int,
    val absentCount : Int
)
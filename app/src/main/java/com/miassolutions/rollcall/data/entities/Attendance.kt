package com.miassolutions.rollcall.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.miassolutions.rollcall.utils.AttendanceStatus
import java.util.UUID

@Entity(
    tableName = "attendance_table",
    primaryKeys = ["studentId", "date"], //composite primary key
    foreignKeys = [ForeignKey(
        entity = Student::class,
        parentColumns = ["studentId"],
        childColumns = ["studentId"],
        onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"])]

)
data class Attendance(
    val studentId: String,
    val date: String,
    val attendanceStatus: AttendanceStatus
)

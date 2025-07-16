package com.miassolutions.rollcall.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.miassolutions.rollcall.common.AttendanceStatus
import java.time.LocalDate

@Entity(
    tableName = "attendance_table",
    primaryKeys = ["studentId", "date"], //composite primary key
    foreignKeys = [ForeignKey(
        entity = StudentEntity::class,
        parentColumns = ["studentId"],
        childColumns = ["studentId"],
        onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["studentId"])]

)
data class AttendanceEntity(
    val studentId: String,
    val date: LocalDate,
    val attendanceStatus: AttendanceStatus
)

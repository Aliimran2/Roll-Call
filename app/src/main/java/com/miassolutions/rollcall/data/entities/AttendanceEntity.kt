package com.miassolutions.rollcall.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.miassolutions.rollcall.utils.AttendanceStatus
import java.util.Date

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
    val date: Long,
    val attendanceStatus: AttendanceStatus
)

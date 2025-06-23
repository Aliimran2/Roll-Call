package com.miassolutions.rollcall.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.miassolutions.rollcall.common.AttendanceStatus

@Entity(
    tableName = "attendance_table",
    primaryKeys = ["studentId", "date"], //composite primary key
    foreignKeys = [
        ForeignKey(
        entity = StudentEntity::class,
        parentColumns = ["studentId"],
        childColumns = ["studentId"],
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            ClassEntity::class,
            parentColumns = ["classId"],
            childColumns = ["classId"],
            onDelete = ForeignKey.CASCADE
        )

    ],
    indices = [
        Index(value = ["studentId"]),
        Index(value = ["studentId"]),
        Index(value = ["date"])
    ]

)
data class AttendanceEntity(

    val classId : String,
    val studentId: String,
    val date: Long,
    val attendanceStatus: AttendanceStatus,
)

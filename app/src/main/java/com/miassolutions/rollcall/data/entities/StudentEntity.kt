package com.miassolutions.rollcall.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "student_table",
    foreignKeys = [ForeignKey(
        entity = ClassEntity::class,
        parentColumns = ["classId"],
        childColumns = ["classId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["regNumber"], unique = true),
        Index(value = ["classId","rollNumber"], unique = true)
    ]
)
data class StudentEntity(
    @PrimaryKey
    val studentId: String = UUID.randomUUID().toString(),
    val studentImage: String? = null,
    val regNumber: String,
    val rollNumber: Int,
    val studentName: String,
    val fatherName: String,
    val bForm: String? = null,
    val dob: Long,
    val doa: Long? = null,
    val classId: String,
    val phoneNumber: String? = null,
    val address: String? = null,

    )

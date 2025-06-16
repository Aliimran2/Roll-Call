package com.miassolutions.rollcall.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "student_table",
    indices = [
        Index(value = ["regNumber"], unique = true),
        Index(value = ["rollNumber"], unique = true)
    ]
)
data class StudentEntity(
    @PrimaryKey
    val studentId: String = UUID.randomUUID().toString(),
    val studentImage : String?= null,
    val regNumber: Int,
    val rollNumber: Int,
    val studentName: String,
    val fatherName: String,
    val bForm: String? = null,
    val dob: Long,
    val doa: Long? =null,
    val klass: String? = null,
    val phoneNumber: String? = null,
    val address: String? = null,
)

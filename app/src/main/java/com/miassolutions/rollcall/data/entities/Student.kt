package com.miassolutions.rollcall.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "student_table",
    indices = [
        Index(value = ["regNumber"], unique = true),
        Index(value = ["rollNumber"], unique = true)
    ]
)
data class Student(
    @PrimaryKey
    val studentId: String = UUID.randomUUID().toString(),
    val regNumber: Int,
    val rollNumber: Int,
    val studentName: String,
    val fatherName: String,
    val dob: String = "",
    val klass: String = "",
    val phoneNumber: String = "",
    val address: String = ""
)

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
    val regNumber: Int = 0,
    val rollNumber: Int,
    val studentName: String,
    val fatherName: String = "Adam",
    val dob: String = "01/01/2000",
    val klass: String = "8th B",
    val phoneNumber: String = "03127430906",
    val address: String = "241 JB"
)

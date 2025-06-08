package com.miassolutions.rollcall.data.entities

import java.util.UUID

data class Student(
    val id: String = UUID.randomUUID().toString(),
    val regNumber: Int = 0,
    val rollNumber: Int,
    val studentName: String,
    val fatherName: String = "Adam",
    val dob: String = "01/01/2000",
    val klass: String = "8th B",
    val phoneNumber: String = "03127430906",
    val address: String = "241 JB"
)

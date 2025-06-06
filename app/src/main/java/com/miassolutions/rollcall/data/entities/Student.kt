package com.miassolutions.rollcall.data.entities

data class Student(
    val id: Int,
    val regNumber: Int = 0,
    val rollNumber: Int,
    val studentName: String,
    val fatherName: String = "",
    val dob: String = "",
    val klass: String = "",
    val phoneNumber: String = "",
    val address: String = ""
)

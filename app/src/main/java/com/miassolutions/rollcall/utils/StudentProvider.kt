package com.miassolutions.rollcall.utils

import androidx.collection.mutableFloatFloatMapOf
import com.miassolutions.rollcall.data.entities.Student

object StudentProvider {

    val students = mutableListOf<Student>()

    fun addStudent(rollNum: Int, studentName: String) {
        val student = Student(rollNumber = rollNum, studentName = studentName)
        students.add(student)
    }

    init {
        addStudent(1, "Ali Imran")
        addStudent(2, "Shan Mumtaz")
        addStudent(3, "Irfan Mumtaz")
        addStudent(4, "Arslan Mumtaz")
    }
}
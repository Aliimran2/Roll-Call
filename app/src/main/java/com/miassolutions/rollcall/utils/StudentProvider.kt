package com.miassolutions.rollcall.utils

import androidx.collection.mutableFloatFloatMapOf
import com.miassolutions.rollcall.data.entities.Student

object StudentProvider {

    val students = mutableListOf<Student>()

    fun addStudent(rollNum: Int, studentName: String) {
        val student = Student(rollNumber = rollNum, studentName = studentName)
        students.add(student)
    }

    fun deleteStudent(id: String) {
        students.removeIf { student: Student ->
            student.id == id
        }
    }

    fun updateStudent(id: String, rollNum: Int, studentName: String) {
        val index = students.indexOfFirst { it.id == id }
        val existingStudent = students[index]
        val updated = existingStudent.copy(studentName = studentName, rollNumber = rollNum)
        students[index] = updated
    }


    init {
        addStudent(1, "Ali Imran")
        addStudent(2, "Shan Mumtaz")
        addStudent(3, "Irfan Mumtaz")
        addStudent(4, "Arslan Mumtaz")

    }
}
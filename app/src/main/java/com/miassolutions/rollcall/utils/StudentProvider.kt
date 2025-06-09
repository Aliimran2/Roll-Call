package com.miassolutions.rollcall.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.data.entities.StudentWithAttendance
import java.time.LocalDate


object StudentProvider {

    val students = mutableListOf<Student>()

    fun addStudent(regNum: Int, rollNum: Int, studentName: String, fatherName: String) {
        val student = Student(
            rollNumber = rollNum,
            studentName = studentName,
            regNumber = regNum,
            fatherName = fatherName,
        )
        students.add(student)
    }

    fun deleteStudent(id: String) {
        students.removeIf { student: Student ->
            student.studentId == id
        }
    }

    fun findStudent(id: String): Student {
        val studentIdx = students.indexOfFirst { it.studentId == id }
        return students[studentIdx]
    }

    fun updateStudent(id: String, rollNum: Int, studentName: String) {
        val index = students.indexOfFirst { it.studentId == id }
        val existingStudent = students[index]
        val updated = existingStudent.copy(studentName = studentName, rollNumber = rollNum)
        students[index] = updated
    }

    data class StudentAttendance(
        val studentId: String,
        val attendanceStatus: AttendanceStatus,
        val date: String // Format: yyyy-MM-dd
    )


    private val attendanceRecords = mutableListOf<StudentAttendance>()

    @RequiresApi(Build.VERSION_CODES.O)
    fun markAttendance(
        studentId: String,
        attendanceStatus: AttendanceStatus,
        date: String = LocalDate.now().toString()
    ) {
        attendanceRecords.removeIf { it.studentId == studentId && it.date == date }
        attendanceRecords.add(StudentAttendance(studentId, attendanceStatus, date))
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getStudentListForToday(): List<StudentWithAttendance> {
        val today = LocalDate.now().toString()
        return students.map {
            val status = getStudentAttendanceOnDate(it.studentId, today) ?: AttendanceStatus.PRESENT
            StudentWithAttendance(
                rollNum = it.rollNumber,
                studentName = it.studentName,
                attendanceStatus = status
            )
        }
    }

    fun getAttendanceForStudents(studentId: String): List<StudentAttendance> {
        return attendanceRecords.filter { it.studentId == studentId }
    }

    fun getAttendanceForDate(date: String): List<StudentAttendance> {
        return attendanceRecords.filter { it.date == date }
    }

    fun getStudentAttendanceOnDate(studentId: String, date: String): AttendanceStatus? {
        return attendanceRecords.find { it.studentId == studentId && it.date == date }?.attendanceStatus
    }

    fun getTotalAttendanceForStudent(studentId: String): Map<AttendanceStatus, Int> {
        return attendanceRecords.filter { it.studentId == studentId }
            .groupingBy { it.attendanceStatus }
            .eachCount()
    }

    fun getClassAttendanceSummary(): Map<AttendanceStatus, Int> {
        return attendanceRecords.groupingBy { it.attendanceStatus }.eachCount()
    }


    init {
        addStudent(1, 1, "Ali Imran", "Mumtaz Hussain")
        addStudent(2, 2, "Shan Mumtaz", "Mumtaz Hussain")
        addStudent(3, 3, "Irfan Mumtaz", "Mumtaz Hussain")
        addStudent(4, 4, "Arslan Mumtaz", "Mumtaz Hussain")

    }
}
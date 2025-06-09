package com.miassolutions.rollcall.data.repository

import com.miassolutions.rollcall.data.dao.StudentDao
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.utils.StudentInsertResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class StudentFetchResult<out T> {
    data class Success<out T>(val data: T) : StudentFetchResult<T>()
    data class Error(val message: String) : StudentFetchResult<Nothing>()
    data object Loading : StudentFetchResult<Nothing>()
}


class Repository @Inject constructor(private val studentDao: StudentDao) {


    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()

    suspend fun getStudentById(studentId: String): StudentFetchResult<Student> {
        return try {
            val student = studentDao.getStudentById(studentId)
            if (student != null) {
                StudentFetchResult.Success(student)
            } else {
                StudentFetchResult.Error("Student not found!!")
            }
        } catch (e: Exception) {
            StudentFetchResult.Error("Failed to fetch student: ${e.localizedMessage ?: "Unknown error"}")
        }


    }

    suspend fun insertStudent(student: Student): StudentInsertResult {
        return try {
            val existingStudent =
                studentDao.getStudentByRollAndRegNum(student.rollNumber, student.regNumber)
            if (existingStudent == null) {
                studentDao.insertStudent(student)
                StudentInsertResult.Success
            } else {
                StudentInsertResult.Duplicate
            }
        } catch (e: Exception) {
            StudentInsertResult.Error("${e.message}")
        }

    }

}
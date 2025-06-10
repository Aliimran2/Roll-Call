package com.miassolutions.rollcall.data.repository

import android.util.Log
import com.miassolutions.rollcall.data.dao.StudentDao
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.utils.DUPLICATE_REG
import com.miassolutions.rollcall.utils.DUPLICATE_ROLL
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
        val duplicateRegNum = studentDao.getStudentByRollNum(student.regNumber)
        val duplicateRollNum = studentDao.getStudentByRollNum(student.rollNumber)

        return when {
                duplicateRegNum != null -> StudentInsertResult.Failure(DUPLICATE_REG)
            duplicateRollNum != null -> StudentInsertResult.Failure(DUPLICATE_ROLL)

            else -> {
                try {
                    studentDao.insertStudent(student)
                    StudentInsertResult.Success
                } catch (e : Exception){
                    StudentInsertResult.Failure(e.localizedMessage ?: "Unknown error")
                }
            }
        }
    }

    suspend fun insertStudents(students : List<Student>) : Boolean {
        return withContext(Dispatchers.IO){
            try {
                studentDao.insertAllStudent(students)
                true
            } catch (e:Exception){
                Log.e("Repository", "Error inserting students", e)
                false
            }
        }
    }

    suspend fun deleteAll(students : List<Student>) = studentDao.deleteAllStudent(students)


    suspend fun deleteStudentById(studentId: String) = studentDao.deleteStudentById(studentId)

}
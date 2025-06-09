package com.miassolutions.rollcall.data.repository

import com.miassolutions.rollcall.data.dao.StudentDao
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.utils.StudentInsertResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor(private val studentDao: StudentDao) {


    val allStudents : Flow<List<Student>> = studentDao.getAllStudents()

    suspend fun insertStudent(student: Student) : StudentInsertResult {
        return try {
            val existingStudent = studentDao.getStudentByRollAndRegNum(student.rollNumber, student.regNumber)
            if (existingStudent == null){
                studentDao.insertStudent(student)
                StudentInsertResult.Success
            } else {
                StudentInsertResult.Duplicate
            }
        } catch (e : Exception){
            StudentInsertResult.Error("${e.message}")
        }

    }

}
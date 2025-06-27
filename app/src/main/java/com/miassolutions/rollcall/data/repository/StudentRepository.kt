package com.miassolutions.rollcall.data.repository

import com.miassolutions.rollcall.common.InsertResult
import com.miassolutions.rollcall.common.OperationResult
import com.miassolutions.rollcall.data.entities.StudentEntity
import kotlinx.coroutines.flow.Flow


interface StudentRepository {
    val allStudentsFlow: Flow<List<StudentEntity>>

    suspend fun insertStudent(student: StudentEntity): InsertResult

    suspend fun insertStudentsBulk(students: List<StudentEntity>): Pair<Int, Int>

    suspend fun getStudentById(id: String): OperationResult<StudentEntity>

    suspend fun updateStudent(student: StudentEntity)

    fun searchStudents(query: String): Flow<List<StudentEntity>>

    suspend fun clearAllStudents()

    suspend fun deleteStudentById(id: String)

    suspend fun getStudentListByClassId(classId: String): Flow<List<StudentEntity>>
}

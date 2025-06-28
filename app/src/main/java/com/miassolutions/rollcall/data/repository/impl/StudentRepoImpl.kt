package com.miassolutions.rollcall.data.repository.impl

import com.miassolutions.rollcall.common.InsertResult
import com.miassolutions.rollcall.common.OperationResult
import com.miassolutions.rollcall.data.dao.StudentDao
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.data.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StudentRepoImpl @Inject constructor(private val studentDao: StudentDao) : StudentRepository {

    override val allStudentsFlow: Flow<List<StudentEntity>>
        get() = studentDao.getAllStudents()

    override suspend fun insertStudent(student: StudentEntity): InsertResult {

        val duplicateReg = studentDao.getStudentByRegNum(student.regNumber)
        val duplicateRoll = studentDao.getStudentByRollNum(student.classId, student.rollNumber)

        return when {
            duplicateReg != null -> InsertResult.Failure("Duplicate Reg no")
            duplicateRoll != null -> InsertResult.Failure("Duplicate Roll no in the class")
            else -> {
                try {
                    studentDao.insertStudent(student)
                    InsertResult.Success
                } catch (e: Exception) {
                    InsertResult.Failure("Failed to insert student, Error : ${e.localizedMessage}")
                }
            }
        }

    }

    override suspend fun insertStudentsBulk(students: List<StudentEntity>): Pair<Int, Int> {
        var success = 0
        var failure = 0

        students.forEach { student ->
            when (insertStudent(student)) {
                is InsertResult.Failure -> failure++
                InsertResult.Success -> success++
            }
        }
        return success to failure
    }

    override suspend fun getStudentById(id: String): OperationResult<StudentEntity> {
        return try {
            studentDao.getStudentById(id)?.let {
                OperationResult.Success(it)
            } ?: OperationResult.Error("Student Not Found")
        } catch (e: Exception) {
            OperationResult.Error("Failed : ${e.localizedMessage}")
        }
    }

    override suspend fun updateStudent(student: StudentEntity) = studentDao.updateStudent(student)


    override fun searchStudents(query: String): Flow<List<StudentEntity>> =
        studentDao.searchStudent(query)

    override suspend fun clearAllStudents() = studentDao.clearAllStudents()

    override suspend fun deleteStudentById(id: String) = studentDao.deleteStudentById(id)

    override suspend fun getStudentListByClassId(classId: String): Flow<List<StudentEntity>> {
        return studentDao.getStudentListByClassId(classId)
    }
}
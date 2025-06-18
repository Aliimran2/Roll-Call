package com.miassolutions.rollcall.data.repository

import android.util.Log
import com.miassolutions.rollcall.data.dao.AttendanceDao
import com.miassolutions.rollcall.data.dao.StudentDao
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.utils.Constants.DUPLICATE_REG_NUMBER
import com.miassolutions.rollcall.utils.Constants.DUPLICATE_ROLL_NUMBER
import com.miassolutions.rollcall.utils.StudentInsertResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed class StudentFetchResult<out T> {
    data class Success<out T>(val data: T) : StudentFetchResult<T>()
    data class Error(val message: String) : StudentFetchResult<Nothing>()
    data object Loading : StudentFetchResult<Nothing>()
}


class Repository @Inject constructor(
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao,
) {

    val allStudents: Flow<List<StudentEntity>> = studentDao.getAllStudents()

    suspend fun getStudentById(studentId: String): StudentFetchResult<StudentEntity> {
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

    suspend fun updateStudent(studentEntity: StudentEntity) =
        studentDao.updateStudent(studentEntity)

    suspend fun insertStudent(studentEntity: StudentEntity): StudentInsertResult {
        val duplicateRegNum = studentDao.getStudentByRegNum(studentEntity.regNumber)
        val duplicateRollNum = studentDao.getStudentByRollNum(studentEntity.rollNumber)

        return when {
            duplicateRegNum != null -> StudentInsertResult.Failure(DUPLICATE_REG_NUMBER)
            duplicateRollNum != null -> StudentInsertResult.Failure(DUPLICATE_ROLL_NUMBER)

            else -> {
                try {
                    studentDao.insertStudent(studentEntity)
                    StudentInsertResult.Success
                } catch (e: Exception) {
                    StudentInsertResult.Failure(e.localizedMessage ?: "Unknown error")
                }
            }
        }
    }

    suspend fun insertStudents(studentEntities: List<StudentEntity>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                studentDao.insertAllStudent(studentEntities)
                true
            } catch (e: Exception) {
                Log.e("Repository", "Error inserting students", e)
                false
            }
        }
    }

    fun searchStudents(query : String) = studentDao.searchStudent(query)


    suspend fun clearAllStudents() = studentDao.clearAllStudents()

    suspend fun deleteStudentById(studentId: String) = studentDao.deleteStudentById(studentId)


    //attendance operations

    suspend fun insertAttendance(attendanceEntity: AttendanceEntity) {
        attendanceDao.insertAttendance(attendanceEntity)
    }

    suspend fun insertAttendances(attendanceEntityList: List<AttendanceEntity>) {
        attendanceDao.insertAttendances(attendanceEntityList)
    }

    suspend fun isAttendanceTaken(date: Long): Boolean {
        return attendanceDao.getAttendanceCountForDate(date) > 0
    }

    fun getAttendanceGroupedByDate(): Flow<Map<Long, List<AttendanceEntity>>> {
        return attendanceDao.getAllAttendances().map { attendList ->
            attendList.groupBy { it.date }
        }
    }

    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceByStudent(studentId)
    }

    suspend fun getAttendanceForDate(date: Long): List<AttendanceEntity> {
        return attendanceDao.getAttendanceForDate(date)
    }

    suspend fun updateAttendance(attendance: AttendanceEntity) {
        attendanceDao.updateAttendance(attendance)
    }

    suspend fun updateAttendances(attendanceList: List<AttendanceEntity>) {
        attendanceDao.updateAttendances(attendanceList)
    }



    suspend fun deleteAttendanceForStudent(studentId: String) {
        attendanceDao.deleteAttendanceForStudent(studentId)
    }

    suspend fun deleteAttendanceForDate(date: Long) {
        attendanceDao.deleteAttendanceForDate(date)
    }

    suspend fun deleteAllAttendance() {
        attendanceDao.deleteAllAttendance()
    }


    suspend fun replaceAttendanceForDate(date: Long, updatedList: List<AttendanceEntity>) {
        // Step 1: Delete old attendance for that date
        attendanceDao.deleteAttendanceForDate(date)

        // Step 2: Insert updated list
        attendanceDao.insertAttendances(updatedList)
    }


}
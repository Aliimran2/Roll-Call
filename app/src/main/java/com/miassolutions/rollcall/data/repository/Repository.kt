package com.miassolutions.rollcall.data.repository

import android.util.Log
import com.miassolutions.rollcall.common.Constants.DUPLICATE_REG_NUMBER
import com.miassolutions.rollcall.common.Constants.DUPLICATE_ROLL_NUMBER
import com.miassolutions.rollcall.data.dao.AttendanceDao
import com.miassolutions.rollcall.data.dao.ClassDao
import com.miassolutions.rollcall.data.dao.StudentDao
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.utils.StudentInsertResult
import com.miassolutions.rollcall.utils.StudentResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Repository @Inject constructor(
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao,
    private val classDao: ClassDao,
) {

    // StudentEntity Operations
    val allStudentsFlow: Flow<List<StudentEntity>> = studentDao.getAllStudents()

    suspend fun insertStudent(studentEntity: StudentEntity): StudentInsertResult {
        val duplicateRegNum = studentDao.getStudentByRegNum(studentEntity.regNumber)
        val duplicateRollNum = studentDao.getStudentByRollNum(studentEntity.classId,studentEntity.rollNumber)

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

    suspend fun getStudentById(studentId: String): StudentResult<StudentEntity> {
        return try {
            val student = studentDao.getStudentById(studentId)
            if (student != null) {
                StudentResult.Success(student)
            } else {
                StudentResult.Error("Student not found!!")
            }
        } catch (e: Exception) {
            StudentResult.Error("Failed to fetch student: ${e.localizedMessage ?: "Unknown error"}")
        }


    }

    suspend fun updateStudent(studentEntity: StudentEntity) =
        studentDao.updateStudent(studentEntity)


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


    fun searchStudents(query: String) = studentDao.searchStudent(query)

    suspend fun clearAllStudents() = studentDao.clearAllStudents()

    suspend fun deleteStudentById(studentId: String) = studentDao.deleteStudentById(studentId)

    suspend fun insertStudentsBulk(students: List<StudentEntity>): Pair<Int, Int> {
        var success = 0
        var failure = 0

        for (student in students) {
            when (insertStudent(student)) {
                is StudentInsertResult.Success -> success++
                is StudentInsertResult.Failure -> failure++
            }
        }

        return success to failure
    }

    //attendance operations


    suspend fun insertAttendances(attendanceEntityList: List<AttendanceEntity>) {
        attendanceDao.insertAttendances(attendanceEntityList)
    }
//
//    suspend fun isAttendanceTaken(date: Long): Boolean {
//        return attendanceDao.getAttendanceCountForDate(date) > 0
//    }
//
//    fun getAttendanceGroupedByDate(): Flow<Map<Long, List<AttendanceEntity>>> {
//        return attendanceDao.getAllAttendances().map { attendList ->
//            attendList.groupBy { it.date }
//        }
//    }
//
//    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceEntity>> {
//        return attendanceDao.getAttendanceByStudent(studentId)
//    }
//
//    suspend fun getAttendanceForDate(date: Long): List<AttendanceEntity> {
//        return attendanceDao.getAttendanceForDate(date)
//    }

    suspend fun updateAttendance(attendance: AttendanceEntity) {
        attendanceDao.updateAttendance(attendance)
    }

    suspend fun updateAttendances(attendanceList: List<AttendanceEntity>) {
        attendanceDao.updateAttendances(attendanceList)
    }


    suspend fun deleteAttendanceForStudent(studentId: String) {
        attendanceDao.deleteAttendanceForStudent(studentId)
    }

//    suspend fun deleteAttendanceForDate(date: Long) {
//        attendanceDao.deleteAttendanceForDate(date)
//    }

    suspend fun deleteAllAttendance() {
        attendanceDao.deleteAllAttendance()
    }

//
//    suspend fun replaceAttendanceForDate(date: Long, updatedList: List<AttendanceEntity>) {
//        // Step 1: Delete old attendance for that date
//        attendanceDao.deleteAttendanceForDate(date)
//
//        // Step 2: Insert updated list
//        attendanceDao.insertAttendances(updatedList)
//    }


    //classEntity Operations

    suspend fun insertClass(classEntity: ClassEntity) = classDao.insetClass(classEntity)

    suspend fun copyClass(classEntity: ClassEntity) {
        classDao.getClassById(classEntity.classId)?.let {
            val newClass = ClassEntity(

                className = classEntity.className,
                sectionName = classEntity.sectionName,
                startDate = classEntity.startDate,
                endDate = classEntity.endDate,
                teacher = classEntity.teacher
            )

            insertClass(newClass)
        }
    }

    fun getClassById(classId: String): Flow<ClassEntity?> {
        return classDao.getClassById(classId)
    }


    suspend fun updateClass(classEntity: ClassEntity) = classDao.updateClass(classEntity)

    suspend fun deleteClass(classEntity: ClassEntity) = classDao.deleteClass(classEntity)

    fun getClasses(): Flow<List<ClassEntity>> = classDao.getClasses()


}
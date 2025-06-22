package com.miassolutions.rollcall.data.repository

import com.miassolutions.rollcall.common.InsertResult
import com.miassolutions.rollcall.common.OperationResult
import com.miassolutions.rollcall.data.dao.AttendanceDao
import com.miassolutions.rollcall.data.dao.ClassDao
import com.miassolutions.rollcall.data.dao.StudentDao
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.data.entities.StudentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao,
    private val classDao: ClassDao,
) : StudentRepository, AttendanceRepository, ClassRepository {
    override val allStudentsFlow: Flow<List<StudentEntity>>
        get() = studentDao.getAllStudents()

    override suspend fun insertStudent(student: StudentEntity): InsertResult {

        val duplicateReg = studentDao.getStudentByRegNum(student.regNumber)
        val duplicateRoll = studentDao.getStudentByRegNum(student.rollNumber)

        return when {
            duplicateReg != null -> InsertResult.Failure("Duplicate Reg No")
            duplicateRoll != null -> InsertResult.Failure("Duplicate Roll No")
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


    override fun searchStudents(query: String): Flow<List<StudentEntity>> = studentDao.searchStudent(query)

    override suspend fun clearAllStudents() = studentDao.clearAllStudents()

    override suspend fun deleteStudentById(id: String) =studentDao.deleteStudentById(id)

    //end student region

    // region Attendance


    override suspend fun insertAttendance(attendance: AttendanceEntity) = attendanceDao.insertAttendance(attendance)

    override suspend fun insertAttendances(list: List<AttendanceEntity>) = attendanceDao.insertAttendances(list)

    override suspend fun isAttendanceTaken(date: Long): Boolean = attendanceDao.getAttendanceCountForDate(date) > 0

    override fun getAttendanceGroupedByDate(): Flow<Map<Long, List<AttendanceEntity>>> =
        attendanceDao.getAllAttendances().map { it.groupBy { att -> att.date } }

    override fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceEntity>> =
        attendanceDao.getAttendanceByStudent(studentId)

    override suspend fun getAttendanceForDate(date: Long): List<AttendanceEntity> =
        attendanceDao.getAttendanceForDate(date)

    override suspend fun getAttendanceForDateRange(startDate: Long, endDate: Long) {
        attendanceDao.getAttendanceForDateRange(startDate, endDate)
    }

    override suspend fun updateAttendance(attendance: AttendanceEntity) =
        attendanceDao.updateAttendance(attendance)

    override suspend fun updateAttendances(list: List<AttendanceEntity>) =
        attendanceDao.updateAttendances(list)

    override suspend fun deleteAttendanceForStudent(studentId: String) =
        attendanceDao.deleteAttendanceForStudent(studentId)

    override suspend fun deleteAttendanceForDate(date: Long) = attendanceDao.deleteAttendanceForDate(date)

    override suspend fun deleteAllAttendance() = attendanceDao.deleteAllAttendance()

    override suspend fun replaceAttendanceForDate(date: Long, list: List<AttendanceEntity>) {
        attendanceDao.deleteAttendanceForDate(date)
        attendanceDao.insertAttendances(list)
    }
    // endregion


    // region ClassRepository
    override suspend fun insertClass(classEntity: ClassEntity) =
        classDao.insetClass(classEntity)

    override suspend fun updateClass(classEntity: ClassEntity) =
        classDao.updateClass(classEntity)

    override suspend fun deleteClass(classEntity: ClassEntity) =
        classDao.deleteClass(classEntity)

    override fun getClasses(): Flow<List<ClassEntity>> =
        classDao.getClasses()
    // endregion

}
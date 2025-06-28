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
        val duplicateRoll = studentDao.getStudentByRollNum(student.classId,student.rollNumber)

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
    override suspend fun getStudentListByClassId(classId: String): Flow<List<StudentEntity>> {
        TODO("Not yet implemented")
    }


    //end student region

    // region Attendance

    override suspend fun insertAttendances(list: List<AttendanceEntity>) {
        attendanceDao.insertAttendances(list)
    }

    override suspend fun isAttendanceTaken(date: Long): Boolean {
        return attendanceDao.getAttendanceCount(date) > 0
    }





    override suspend fun getClassAttendanceGroupedByDate(classId: String): Flow<Map<Long, List<AttendanceEntity>>> {
        return attendanceDao.getClassAttendances(classId).map { it.groupBy { att -> att.date } }
    }

    override suspend fun getClassAttendanceForDate(date: Long): List<AttendanceEntity> {
        return attendanceDao.getClassAttendanceForDate(date)
    }

    override suspend fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceEntity>> {
        return attendanceDao.getAttendanceByStudent(studentId)
    }

    override suspend fun updateAttendanceList(list: List<AttendanceEntity>) {
        attendanceDao.updateAttendances(list)
    }

    override suspend fun deleteAttendanceForStudent(studentId: String) {
        attendanceDao.deleteAttendanceForStudent(studentId)
    }

    override suspend fun deleteAttendancesForClassAndDate(classId: String, date: Long) {
        attendanceDao.deleteAttendanceForClassAndDate(classId, date)
    }

    override suspend fun replaceAttendanceForDate(
        classId: String,
        date: Long,
        list: List<AttendanceEntity>,
    ) {
        attendanceDao.deleteAttendanceForClassAndDate(classId, date)
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

    override fun getClassById(id: String): Flow<ClassEntity?> {
        TODO("Not yet implemented")
    }

    override suspend fun copyExistingClass(classEntity: ClassEntity) {
        TODO("Not yet implemented")
    }


    // endregion

}
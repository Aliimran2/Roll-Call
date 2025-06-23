package com.miassolutions.rollcall.data.repository.impl

import com.miassolutions.rollcall.data.dao.AttendanceDao
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AttendanceRepoImpl @Inject constructor(private val attendanceDao: AttendanceDao) :
    AttendanceRepository {


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

}
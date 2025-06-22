package com.miassolutions.rollcall.data.repository

import com.miassolutions.rollcall.data.entities.AttendanceEntity
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {

    suspend fun insertAttendance(attendance: AttendanceEntity)
    suspend fun insertAttendances(list: List<AttendanceEntity>)
    suspend fun isAttendanceTaken(date: Long): Boolean
    fun getAttendanceGroupedByDate(): Flow<Map<Long, List<AttendanceEntity>>>
    fun getAttendanceForStudent(studentId: String): Flow<List<AttendanceEntity>>
    suspend fun getAttendanceForDate(date: Long): List<AttendanceEntity>
    suspend fun getAttendanceForDateRange(startDate : Long, endDate : Long)
    suspend fun updateAttendance(attendance: AttendanceEntity)
    suspend fun updateAttendances(list: List<AttendanceEntity>)
    suspend fun deleteAttendanceForStudent(studentId: String)
    suspend fun deleteAttendanceForDate(date: Long)
    suspend fun deleteAllAttendance()
    suspend fun replaceAttendanceForDate(date: Long, list: List<AttendanceEntity>)
}
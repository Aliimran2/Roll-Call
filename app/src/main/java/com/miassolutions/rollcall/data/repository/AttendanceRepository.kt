package com.miassolutions.rollcall.data.repository

import com.miassolutions.rollcall.data.entities.AttendanceEntity
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {

    suspend fun insertAttendances(list: List<AttendanceEntity>)

    suspend fun isAttendanceTaken(classId: String,date: Long) : Boolean

//    suspend fun getAttendanceCountForClassAndDate(classId : String, date :Long)

    suspend fun getClassAttendanceGroupedByDate(classId: String) : Flow<Map<Long, List<AttendanceEntity>>>

    suspend fun getClassAttendanceForDate(date: Long) : List<AttendanceEntity>

    suspend fun getAttendanceByStudent(studentId : String) : Flow<List<AttendanceEntity>>

    suspend fun updateAttendanceList(list: List<AttendanceEntity>)

    suspend fun deleteAttendanceForStudent(studentId: String)

    suspend fun deleteAttendancesForClassAndDate(classId: String, date: Long)

    suspend fun replaceAttendanceForDate(classId: String, date: Long, list: List<AttendanceEntity>)

}
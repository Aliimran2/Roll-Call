package com.miassolutions.rollcall.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.common.AttendanceStatus
import kotlinx.coroutines.flow.Flow


@Dao
interface AttendanceDao {
    // --- Insert Operations ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(attendanceEntityList: List<AttendanceEntity>)

    // --- Get Attendance Data ---
    // for prevent duplication
    @Query("SELECT COUNT(*) FROM attendance_table WHERE classId =:classId AND date = :date")
    suspend fun getAttendanceCountForClassAndDate(classId: String, date: Long): Int

    @Query("SELECT * FROM attendance_table WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_table WHERE classId = :classId ORDER BY date DESC")
    fun getClassAttendances(classId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_table WHERE classId = :classId ORDER BY studentId ASC")
    suspend fun getClassAttendanceForDate(classId: String): List<AttendanceEntity>

//    @Query("SELECT * FROM attendance_table WHERE classId =:classId AND date BETWEEN :startDate AND :endDate")
//    fun getClassAttendanceForDateRange(classId : String,startDate: Long, endDate: Long): Flow<List<AttendanceEntity>>


    @Query("SELECT COUNT(*) FROM attendance_table WHERE date = :date AND attendanceStatus = :status")
    fun getAttendanceCountForDateAndStatus(date: Long, status: AttendanceStatus): Flow<Int>


    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Update
    suspend fun updateAttendances(attendanceList: List<AttendanceEntity>)

    //     --- Delete Operations ---

    @Query("DELETE FROM attendance_table WHERE classId =:classId AND date =:date")
    suspend fun deleteAttendanceForClassAndDate(classId : String,date: Long)

    @Query("DELETE FROM attendance_table WHERE studentId = :studentId")
    suspend fun deleteAttendanceForStudent(studentId: String)

    @Query("DELETE FROM attendance_table")
    suspend fun deleteAllAttendance()
}
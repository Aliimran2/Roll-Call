package com.miassolutions.rollcall.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.miassolutions.rollcall.common.AttendanceStatus
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface AttendanceDao {
    // --- Insert Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendanceEntity: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(attendanceEntityList: List<AttendanceEntity>)

    // --- Get Attendance Data ---

    @Query("SELECT COUNT(*) FROM attendance_table WHERE date = :date")
    fun getAttendanceCountForDate(date: Long): Flow<Int>




    @Query("SELECT * FROM attendance_table WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_table WHERE date = :date ORDER BY studentId ASC")
    suspend fun getAttendanceForDate(date: Long): List<AttendanceEntity>

    @Query("SELECT * FROM attendance_table ORDER BY date DESC")
    fun getAllAttendances(): Flow<List<AttendanceEntity>>

    @Query("SELECT COUNT(*) FROM attendance_table WHERE date = :date")
    fun getMarkedStudentsCountForDate(date: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance_table WHERE date = :date AND attendanceStatus = :status")
    fun getAttendanceCountForDateAndStatus(date: Long, status: AttendanceStatus): Flow<Int>

    @Query("SELECT COUNT(*) FROM student_table")
    fun getTotalStudentsCount(): Flow<Int>

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Update
    suspend fun updateAttendances(attendanceList: List<AttendanceEntity>)

    //     --- Delete Operations ---

    @Query("DELETE FROM attendance_table WHERE date =:date")
    suspend fun deleteAttendanceForDate(date: Long)

    @Query("DELETE FROM attendance_table WHERE studentId = :studentId")
    suspend fun deleteAttendanceForStudent(studentId: String)

    @Query("DELETE FROM attendance_table")
    suspend fun deleteAllAttendance()
}
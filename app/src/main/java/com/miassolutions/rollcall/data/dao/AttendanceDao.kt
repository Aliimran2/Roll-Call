package com.miassolutions.rollcall.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.miassolutions.rollcall.common.AttendanceStatus
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.entities.StudentEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate


@Dao
interface AttendanceDao {
    // --- Insert Operations ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendanceEntity: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(attendanceEntityList: List<AttendanceEntity>)

    // --- Get Attendance Data ---

    @Query("SELECT COUNT(*) FROM attendance_table WHERE date = :date")
    fun getAttendanceCountForDate(date: LocalDate): Flow<Int>


    @Query("SELECT * FROM attendance_table WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_table WHERE date = :date ORDER BY studentId ASC")
    suspend fun getAttendanceForDate(date: LocalDate): List<AttendanceEntity>

    @Query("SELECT * FROM attendance_table WHERE date = :date ORDER BY studentId ASC")
    fun searchAttendanceForDate(date: LocalDate): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_table WHERE :startDate AND :endDate")
    fun getClassAttendancesBetweenDates(
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_table WHERE studentId=:studentId AND :startDate AND :endDate")
    fun getStudentAttendancesBetweenDates(
        studentId: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance_table ORDER BY date DESC")
    fun getAllAttendances(): Flow<List<AttendanceEntity>>

    @Query("SELECT COUNT(*) FROM attendance_table WHERE date = :date")
    fun getMarkedStudentsCountForDate(date: LocalDate): Flow<Int>

    @Query("SELECT COUNT(*) FROM attendance_table WHERE date = :date AND attendanceStatus = :status")
    fun getAttendanceCountForDateAndStatus(date: LocalDate, status: AttendanceStatus): Flow<Int>

    @Query("SELECT COUNT(*) FROM student_table")
    fun getTotalStudentsCount(): Flow<Int>

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)

    @Update
    suspend fun updateAttendances(attendanceList: List<AttendanceEntity>)


    @Query("DELETE FROM attendance_table WHERE date =:date")
    suspend fun deleteAttendanceForDate(date: LocalDate)

    @Query("DELETE FROM attendance_table WHERE studentId = :studentId")
    suspend fun deleteAttendanceForStudent(studentId: String)

    @Query("DELETE FROM attendance_table")
    suspend fun deleteAllAttendance()
}
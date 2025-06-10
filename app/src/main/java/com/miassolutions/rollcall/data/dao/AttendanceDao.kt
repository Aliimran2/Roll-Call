package com.miassolutions.rollcall.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.miassolutions.rollcall.data.entities.Attendance
import com.miassolutions.rollcall.utils.AttendanceStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: Attendance)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendances(attendanceList: List<Attendance>)

    @Query("SELECT * FROM attendance_table WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceByStudent(studentId: String): Flow<List<Attendance>>

    @Query("SELECT * FROM attendance_table WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<Attendance>>

    @Query("DELETE FROM attendance_table WHERE studentId = :studentId")
    suspend fun deleteAttendanceForStudent(studentId: String)

    // 1️⃣ Get total students who have attendance entry for a specific date
    @Query("SELECT COUNT(*) FROM attendance_table WHERE date = :date")
    fun getTotalMarkedStudentsCount(date: String): Flow<Int>

    // 2️⃣ Get present students count for a specific date
    @Query("SELECT COUNT(*) FROM attendance_table WHERE date = :date AND attendanceStatus = :status")
    fun getPresentStudentsCount(date: String, status: AttendanceStatus = AttendanceStatus.PRESENT): Flow<Int>

    // 3️⃣ Get absent students count for a specific date
    @Query("SELECT COUNT(*) FROM attendance_table WHERE date = :date AND attendanceStatus = :status")
    fun getAbsentStudentsCount(date: String, status: AttendanceStatus = AttendanceStatus.ABSENT): Flow<Int>

    @Query("SELECT COUNT(*) FROM student_table")
    fun getTotalStudentsCount(): Flow<Int>

    @Query("DELETE FROM attendance_table")
    suspend fun deleteAllAttendance()
}
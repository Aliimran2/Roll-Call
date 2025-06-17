package com.miassolutions.rollcall.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.utils.AttendanceStatus
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

    suspend fun getAttendanceCountForDate(date: Long): Int

    /**
     * Retrieves all attendance records for a specific student, ordered by date in descending order.
     * The returned data is a [Flow], meaning it will emit updates whenever the underlying data changes.
     *
     * @param studentId The unique ID of the student.
     * @return A [Flow] emitting a list of [AttendanceEntity] for the specified student.
     */
    @Query("SELECT * FROM attendance_table WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceByStudent(studentId: String): Flow<List<AttendanceEntity>>

    /**
     * Retrieves all attendance records for a specific date, ordered by student ID in ascending order.
     * The returned data is a [Flow], meaning it will emit updates whenever the underlying data changes.
     *
     * @param date The date (as a Long timestamp) for which to retrieve attendance.
     * @return A [Flow] emitting a list of [AttendanceEntity] for the specified date.
     */
    @Query("SELECT * FROM attendance_table WHERE date = :date ORDER BY studentId ASC")
    fun getAttendanceForDate(date: Long): Flow<List<AttendanceEntity>>

    /**
     * Retrieves all attendance records present in the database, ordered by date in descending order.
     * This is a suspend function, meaning it will block until the data is fetched.
     *
     * @return A list of all [AttendanceEntity] objects.
     */
    @Query("SELECT * FROM attendance_table ORDER BY date DESC")
    fun getAllAttendances(): Flow<List<AttendanceEntity>>

    // --- Get Attendance Counts ---

    /**
     * Gets the count of students who have at least one attendance entry (i.e., "marked" students)
     * for a specific date.
     * The returned data is a [Flow], meaning it will emit updates whenever the underlying data changes.
     *
     * @param date The date (as a Long timestamp) for which to get the count.
     * @return A [Flow] emitting the count of marked students for the specified date.
     */
    @Query("SELECT COUNT(*) FROM attendance_table WHERE date = :date")
    fun getMarkedStudentsCountForDate(date: Long): Flow<Int>

    /**
     * Gets the count of students with a specific attendance status (e.g., PRESENT, ABSENT, LATE)
     * for a given date.
     * The returned data is a [Flow], meaning it will emit updates whenever the underlying data changes.
     *
     * @param date The date (as a Long timestamp) for which to get the count.
     * @param status The [AttendanceStatus] to filter by (e.g., [AttendanceStatus.PRESENT]).
     * @return A [Flow] emitting the count of students with the specified status for the given date.
     */
    @Query("SELECT COUNT(*) FROM attendance_table WHERE date = :date AND attendanceStatus = :status")
    fun getAttendanceCountForDateAndStatus(date: Long, status: AttendanceStatus): Flow<Int>

    /**
     * Gets the total count of all students from the 'student_table'.
     * The returned data is a [Flow], meaning it will emit updates whenever the underlying data changes.
     * @return [Flow] emitting the total count of all students.
     */
    @Query("SELECT COUNT(*) FROM student_table")
    fun getTotalStudentsCount(): Flow<Int>

    @Update
    suspend fun updateAttendance(attendance: AttendanceEntity)


    @Update
    suspend fun updateAttendances(attendanceList: List<AttendanceEntity>)












    // --- Delete Operations ---


    // Deletes all attendance records associated with a specific student.

    @Query("DELETE FROM attendance_table WHERE date =:date")
    suspend fun deleteAttendanceForDate(date: Long)


    @Query("DELETE FROM attendance_table WHERE studentId = :studentId")
    suspend fun deleteAttendanceForStudent(studentId: String)


    @Query("DELETE FROM attendance_table")
    suspend fun deleteAllAttendance()
}
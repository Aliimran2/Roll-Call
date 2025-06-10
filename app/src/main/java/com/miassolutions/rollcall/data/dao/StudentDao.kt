package com.miassolutions.rollcall.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.miassolutions.rollcall.data.entities.Student
import kotlinx.coroutines.flow.Flow
@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudent(student: Student)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllStudent(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Delete
    suspend fun deleteAllStudent(students: List<Student>)

    @Query("DELETE FROM student_table WHERE studentId = :studentId")
    suspend fun deleteStudentById(studentId: String)

    @Query("SELECT * FROM student_table WHERE studentId = :studentId LIMIT 1")
    suspend fun getStudentById(studentId: String) : Student?

    @Query("SELECT * FROM student_table ORDER BY rollNumber ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM student_table WHERE regNumber =:regNumber LIMIT 1")
    suspend fun getStudentByRegNum(regNumber: Int) : Student?

    @Query("SELECT * FROM student_table WHERE rollNumber =:rollNumber LIMIT 1")
    suspend fun getStudentByRollNum(rollNumber: Int) : Student?

    @Query(
        """
        SELECT * FROM student_table
        WHERE studentName LIKE '%'||:searchQuery|| '%'
        OR fatherName LIKE '%' || :searchQuery || '%'
        OR phoneNumber LIKE '%' || :searchQuery || '%'
        OR CAST(rollNumber AS TEXT) LIKE '%' || :searchQuery || '%'
        OR CAST(regNumber AS TEXT) LIKE '%' || :searchQuery || '%'
    """
    )
    fun searchStudentByName(searchQuery: String): Flow<List<Student>>
}
package com.miassolutions.rollcall.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.miassolutions.rollcall.data.entities.Student
import kotlinx.coroutines.flow.Flow

interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStudent(student: Student)

    @Update
    fun updateStudent(student: Student)

    @Delete
    fun deleteStudent(student: Student)

    @Query("SELECT * FROM student_table WHERE studentId = :studentId")
    fun deleteStudentById(studentId: String)

    @Query("SELECT * FROM student_table ORDER BY rollNumber ASC")
    fun getAllStudents(): Flow<List<Student>>

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
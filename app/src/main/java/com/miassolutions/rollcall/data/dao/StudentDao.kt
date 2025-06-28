package com.miassolutions.rollcall.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.miassolutions.rollcall.data.entities.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStudent(studentEntity: StudentEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllStudent(studentEntities: List<StudentEntity>)

    @Update
    suspend fun updateStudent(studentEntity: StudentEntity)


    @Delete
    suspend fun deleteStudent(studentEntity: StudentEntity)

    @Query("DELETE FROM student_table")
    suspend fun clearAllStudents()

    @Query("DELETE FROM student_table WHERE studentId = :studentId")
    suspend fun deleteStudentById(studentId: String)

    //getter operations

    @Query("SELECT * FROM student_table WHERE studentId = :studentId LIMIT 1")
    suspend fun getStudentById(studentId: String): StudentEntity?

    @Query("SELECT * FROM student_table ORDER BY rollNumber ASC")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM student_table WHERE classId=:classId ORDER BY rollNumber ASC")
    fun getStudentListByClassId(classId: String): Flow<List<StudentEntity>>

    //duplication check getter

    @Query("SELECT * FROM student_table WHERE regNumber =:regNumber LIMIT 1")
    suspend fun getStudentByRegNum(regNumber: String): StudentEntity?

    @Query("SELECT * FROM student_table WHERE classId =:classId AND rollNumber =:rollNumber LIMIT 1")
    suspend fun getStudentByRollNum(classId: String, rollNumber: Int): StudentEntity?


    //search by name, roll no, reg no
    @Query(
        """
        SELECT * FROM student_table
        WHERE studentName LIKE '%'||:searchQuery|| '%'
        OR CAST(rollNumber AS TEXT) LIKE  :searchQuery || '%'
        OR CAST(regNumber AS TEXT) LIKE :searchQuery || '%'
    """
    )
    fun searchStudent(searchQuery: String): Flow<List<StudentEntity>>
}
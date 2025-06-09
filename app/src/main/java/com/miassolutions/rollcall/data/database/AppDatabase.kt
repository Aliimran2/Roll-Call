package com.miassolutions.rollcall.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.miassolutions.rollcall.data.dao.StudentDao
import com.miassolutions.rollcall.data.entities.Attendance
import com.miassolutions.rollcall.data.entities.Student

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){

    abstract fun studentDao() : StudentDao

}
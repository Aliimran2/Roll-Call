package com.miassolutions.rollcall.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.miassolutions.rollcall.data.converters.AttendanceStatusConverter
import com.miassolutions.rollcall.data.dao.AttendanceDao
import com.miassolutions.rollcall.data.dao.StudentDao
import com.miassolutions.rollcall.data.entities.Attendance
import com.miassolutions.rollcall.data.entities.Student

@Database(entities = [Student::class, Attendance::class], version = 1, exportSchema = false)
@TypeConverters(AttendanceStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao() : AttendanceDao

}
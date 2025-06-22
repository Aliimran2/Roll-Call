package com.miassolutions.rollcall.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.miassolutions.rollcall.data.converters.AttendanceStatusConverter
import com.miassolutions.rollcall.data.dao.AttendanceDao
import com.miassolutions.rollcall.data.dao.ClassDao
import com.miassolutions.rollcall.data.dao.StudentDao
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.data.entities.StudentEntity

@Database(
    entities = [StudentEntity::class, AttendanceEntity::class, ClassEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(AttendanceStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun classDao(): ClassDao

}
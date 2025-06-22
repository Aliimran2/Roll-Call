package com.miassolutions.rollcall.di

import android.content.Context
import androidx.room.Room
import com.miassolutions.rollcall.data.dao.AttendanceDao
import com.miassolutions.rollcall.data.dao.ClassDao
import com.miassolutions.rollcall.data.dao.StudentDao
import com.miassolutions.rollcall.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "AppDatabase"
        ).build()
    }

    @Provides
    @Singleton
    fun providesStudentDao(db: AppDatabase): StudentDao {
        return db.studentDao()
    }

    @Provides
    @Singleton
    fun providesAttendanceDao(db: AppDatabase): AttendanceDao {
        return db.attendanceDao()
    }

    @Provides
    @Singleton
    fun providesClassDao(db: AppDatabase): ClassDao {
        return db.classDao()
    }

}
package com.miassolutions.rollcall.data.repository

import com.miassolutions.rollcall.data.entities.ClassEntity
import kotlinx.coroutines.flow.Flow

interface ClassRepository {

    suspend fun insertClass(classEntity: ClassEntity)

    suspend fun updateClass(classEntity: ClassEntity)

    suspend fun deleteClass(classEntity: ClassEntity)

    fun getClasses(): Flow<List<ClassEntity>>

    fun getClassById(id : String) : Flow<ClassEntity?>

    suspend fun copyExistingClass(classEntity: ClassEntity)

}
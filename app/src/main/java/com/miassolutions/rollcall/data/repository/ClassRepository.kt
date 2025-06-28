package com.miassolutions.rollcall.data.repository

import com.miassolutions.rollcall.common.OperationResult
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.ui.model.ClassWithStudents
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface ClassRepository {

    suspend fun insertClass(classEntity: ClassEntity)

    suspend fun updateClass(classEntity: ClassEntity)

    suspend fun deleteClass(classEntity: ClassEntity)

    fun getClasses(): Flow<List<ClassEntity>>

    fun getClassesWithStudents() : Flow<List<ClassWithStudents>>

    fun getClassById(id : String) : Flow<ClassEntity?>
}
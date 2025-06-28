package com.miassolutions.rollcall.data.repository.impl

import com.miassolutions.rollcall.data.dao.ClassDao
import com.miassolutions.rollcall.data.dao.StudentDao
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.data.repository.ClassRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class ClassRepoImpl @Inject constructor(
    private val classDao: ClassDao,
) : ClassRepository {

    // region ClassRepository
    override suspend fun insertClass(classEntity: ClassEntity) =
        classDao.insertClass(classEntity)

    override suspend fun updateClass(classEntity: ClassEntity) =
        classDao.updateClass(classEntity)

    override suspend fun deleteClass(classEntity: ClassEntity) =
        classDao.deleteClass(classEntity)

    override fun getClasses(): Flow<List<ClassEntity>> =
        classDao.getClasses()

    override fun getClassById(id: String): Flow<ClassEntity?> {
        return classDao.getClassById(id)
    }



}
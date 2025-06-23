package com.miassolutions.rollcall.data.repository.impl

import com.miassolutions.rollcall.data.dao.ClassDao
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.data.repository.ClassRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClassRepoImpl @Inject constructor(private val classDao: ClassDao) : ClassRepository {

    // region ClassRepository
    override suspend fun insertClass(classEntity: ClassEntity) =
        classDao.insetClass(classEntity)

    override suspend fun updateClass(classEntity: ClassEntity) =
        classDao.updateClass(classEntity)

    override suspend fun deleteClass(classEntity: ClassEntity) =
        classDao.deleteClass(classEntity)

    override fun getClasses(): Flow<List<ClassEntity>> =
        classDao.getClasses()


    // endregion
}
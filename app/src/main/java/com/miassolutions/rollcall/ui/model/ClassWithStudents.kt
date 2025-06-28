package com.miassolutions.rollcall.ui.model

import androidx.room.Embedded
import androidx.room.Relation
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.data.entities.StudentEntity

data class ClassWithStudents(
    @Embedded val classEntity: ClassEntity,
    @Relation(
        parentColumn = "classId",
        entityColumn = "classId"
    )
    val students: List<StudentEntity>
)

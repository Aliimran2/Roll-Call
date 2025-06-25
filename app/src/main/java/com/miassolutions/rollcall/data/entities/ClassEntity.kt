package com.miassolutions.rollcall.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "class_table")
data class ClassEntity(
    @PrimaryKey
    val classId : String = UUID.randomUUID().toString(),
    val className : String,
    val sectionName : String? = null,
    val startDate : Long = Date().time,
    val endDate : Long = Date().time,
    val teacher : String
)
package com.miassolutions.rollcall.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class_table")
data class ClassEntity(
    @PrimaryKey(autoGenerate = true)
    val classId : Int = 0,
    val className : String,
    val startDate : Long,
    val endDate : Long,
    val teacher : String
)
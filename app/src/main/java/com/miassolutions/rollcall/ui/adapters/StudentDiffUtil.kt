package com.miassolutions.rollcall.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.miassolutions.rollcall.data.entities.StudentEntity

class StudentDiffUtil : DiffUtil.ItemCallback<StudentEntity>() {
    override fun areItemsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean {
        return oldItem.studentId == newItem.studentId
    }

    override fun areContentsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean {
        return oldItem == newItem
    }
}
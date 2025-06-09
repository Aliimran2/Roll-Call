package com.miassolutions.rollcall.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.miassolutions.rollcall.data.entities.Student

class StudentDiffUtil : DiffUtil.ItemCallback<Student>() {
    override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
        return oldItem.studentId == newItem.studentId
    }

    override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
        return oldItem == newItem
    }
}
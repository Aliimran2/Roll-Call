package com.miassolutions.rollcall.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.miassolutions.rollcall.data.entities.StudentWithAttendance

class AttendanceDiffUtil : DiffUtil.ItemCallback<StudentWithAttendance>() {
    override fun areItemsTheSame(
        oldItem: StudentWithAttendance,
        newItem: StudentWithAttendance
    ): Boolean {
        return oldItem.rollNum == newItem.rollNum
    }

    override fun areContentsTheSame(
        oldItem: StudentWithAttendance,
        newItem: StudentWithAttendance
    ): Boolean {
        return oldItem == newItem
    }
}
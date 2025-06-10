package com.miassolutions.rollcall.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.miassolutions.rollcall.data.entities.MarkAttendanceUiModel

class AttendanceDiffUtil : DiffUtil.ItemCallback<MarkAttendanceUiModel>() {
    override fun areItemsTheSame(
        oldItem: MarkAttendanceUiModel,
        newItem: MarkAttendanceUiModel
    ): Boolean {
        return oldItem.studentId == newItem.studentId
    }

    override fun areContentsTheSame(
        oldItem: MarkAttendanceUiModel,
        newItem: MarkAttendanceUiModel
    ): Boolean {
        return oldItem == newItem
    }

}
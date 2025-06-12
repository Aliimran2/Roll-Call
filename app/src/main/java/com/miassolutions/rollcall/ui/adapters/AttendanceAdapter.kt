package com.miassolutions.rollcall.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miassolutions.rollcall.databinding.ItemAttendanceBinding
import com.miassolutions.rollcall.ui.model.MarkAttendanceUiModel
import com.miassolutions.rollcall.utils.AttendanceStatus

class AttendanceAdapter(
    private val onStatusChanged: (MarkAttendanceUiModel, AttendanceStatus) -> Unit
) :
    ListAdapter<MarkAttendanceUiModel, AttendanceAdapter.AttendanceViewHolder>(AttendanceDiffUtil()) {

    inner class AttendanceViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MarkAttendanceUiModel) {
            binding.apply {
                tvRollNum.text = item.rollNumber.toString()
                tvStudentName.text = item.studentName

                toggleAttendance.setOnCheckedChangeListener(null) // clear old listener

                toggleAttendance.isChecked = item.attendanceStatus == AttendanceStatus.PRESENT

                toggleAttendance.setOnCheckedChangeListener { _, isChecked ->
                    val newState = if (isChecked) AttendanceStatus.PRESENT else AttendanceStatus.ABSENT
                    onStatusChanged(item, newState)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        return AttendanceViewHolder(
            ItemAttendanceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


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
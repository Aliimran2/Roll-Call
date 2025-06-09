package com.miassolutions.rollcall.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miassolutions.rollcall.data.entities.StudentWithAttendance
import com.miassolutions.rollcall.databinding.ItemAttendanceBinding
import com.miassolutions.rollcall.utils.AttendanceStatus

class AttendanceAdapter :
    ListAdapter<StudentWithAttendance, AttendanceAdapter.AttendanceViewHolder>(AttendanceDiffUtil()) {

    inner class AttendanceViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudentWithAttendance) {
            binding.apply {
                tvRollNum.text = item.rollNum.toString()
                tvStudentName.text = item.studentName

                toggleAttendance.isChecked = item.attendanceStatus.name == AttendanceStatus.PRESENT.name


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
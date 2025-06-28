package com.miassolutions.rollcall.ui.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miassolutions.rollcall.databinding.ItemAttendanceBinding
import com.miassolutions.rollcall.ui.model.AttendanceUIModel
import com.miassolutions.rollcall.common.AttendanceStatus

class AttendanceAdapter(
    private val readOnly: Boolean = false,
    private val onStatusChanged: (AttendanceUiState.StudentAttendance, AttendanceStatus) -> Unit,
) :
    ListAdapter<AttendanceUiState.StudentAttendance, AttendanceAdapter.AttendanceViewHolder>(
        AttendanceDiffUtil()
    ) {

    inner class AttendanceViewHolder(private val binding: ItemAttendanceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AttendanceUiState.StudentAttendance) {
            binding.apply {
                tvRollNum.text = item.rollNumber.toString()
                tvStudentName.text = item.name

                toggleAttendance.setOnCheckedChangeListener(null) // clear old listener
                toggleAttendance.isChecked = item.status == AttendanceStatus.PRESENT

                toggleAttendance.isEnabled = !readOnly
                if (!readOnly) {

                    toggleAttendance.setOnCheckedChangeListener { _, isChecked ->
                        val newState =
                            if (isChecked) AttendanceStatus.PRESENT else AttendanceStatus.ABSENT
                        onStatusChanged(item, newState)
                    }
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


class AttendanceDiffUtil : DiffUtil.ItemCallback<AttendanceUiState.StudentAttendance>() {
    override fun areItemsTheSame(
        oldItem: AttendanceUiState.StudentAttendance,
        newItem: AttendanceUiState.StudentAttendance,
    ): Boolean {
        return oldItem.studentId == newItem.studentId
    }

    override fun areContentsTheSame(
        oldItem: AttendanceUiState.StudentAttendance,
        newItem: AttendanceUiState.StudentAttendance,
    ): Boolean {
        return oldItem == newItem
    }


}
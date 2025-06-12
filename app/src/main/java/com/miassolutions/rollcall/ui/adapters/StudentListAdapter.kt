package com.miassolutions.rollcall.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.databinding.ItemStudentBinding

class StudentListAdapter(

    private val onProfileClick: (StudentEntity) -> Unit,
    private val onReportClick: (String) -> Unit,
    private val onEditClick: (StudentEntity) -> Unit,
    private val onDeleteClick: (String) -> Unit,
    private val onPhoneClick: (String) -> Unit
) : ListAdapter<StudentEntity, StudentListAdapter.StudentViewHolder>(StudentDiffUtil()) {

    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudentEntity) {
            binding.apply {
                tvStudentName.text = item.studentName
                tvRegNum.text = "Reg No - ${item.regNumber}"
                tvRollNum.text = "Roll No - ${item.rollNumber}"

                ivProfile.setOnClickListener { onProfileClick(item) }
                ivReport.setOnClickListener { onReportClick(item.studentId) }
                ivEdit.setOnClickListener { onEditClick(item) }
                ivDelete.setOnClickListener { onDeleteClick(item.studentId) }
                ivPhone.setOnClickListener { onPhoneClick(item.phoneNumber) }


            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        return StudentViewHolder(
            ItemStudentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
package com.miassolutions.rollcall.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.bumptech.glide.Glide
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.databinding.ItemStudentBinding
import java.io.File
import kotlin.reflect.KFunction1

class StudentListAdapter(

    private val onProfileClick: (StudentEntity) -> Unit,
    private val onReportClick: (String) -> Unit,
    private val onEditClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit,
    private val onPhoneClick: (String) -> Unit,
) : ListAdapter<StudentEntity, StudentListAdapter.StudentViewHolder>(StudentDiffUtil()) {

    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StudentEntity) {

            binding.apply {

                if (item.studentImage.isNullOrEmpty()) {
                    Glide.with(ivStudent.context)
                        .load(R.drawable.ic_person)
                        .into(ivStudent)
                } else {

                    Glide.with(ivStudent.context)
                        .load(item.studentImage)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_error_image)
                        .into(ivStudent)
                }


                tvStudentName.text = item.studentName
                tvRegNum.text = "Reg No - ${item.regNumber}"
                tvRollNum.text = "Roll No - ${item.rollNumber}"

                ivProfile.setOnClickListener { onProfileClick(item) }
                ivReport.setOnClickListener { onReportClick(item.studentId) }
                ivEdit.setOnClickListener { onEditClick(item.studentId) }
                ivDelete.setOnClickListener { onDeleteClick(item.studentId) }
                ivPhone.setOnClickListener {
                    item.phoneNumber?.let { phoneNum ->
                        onPhoneClick(
                            phoneNum
                        )
                    }
                }


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


class StudentDiffUtil : DiffUtil.ItemCallback<StudentEntity>() {
    override fun areItemsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean {
        return oldItem.studentId == newItem.studentId
    }

    override fun areContentsTheSame(oldItem: StudentEntity, newItem: StudentEntity): Boolean {
        return oldItem == newItem
    }
}
package com.miassolutions.rollcall.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.databinding.ItemClassBinding
import com.miassolutions.rollcall.extenstions.toFormattedDate
import com.miassolutions.rollcall.ui.model.ClassWithStudents

class ClassListAdapter(
    private val onStudentsClick: (ClassEntity) -> Unit,
    private val onAttendanceClick: (ClassEntity) -> Unit,
    private val onReportClick: (ClassEntity) -> Unit,
    private val onMoreClick: (View, ClassEntity) -> Unit,
) : ListAdapter<ClassWithStudents, ClassListAdapter.ClassViewHolder>(ClassDiffUtil) {


    companion object {
        val ClassDiffUtil = object : DiffUtil.ItemCallback<ClassWithStudents>() {
            override fun areItemsTheSame(
                oldItem: ClassWithStudents,
                newItem: ClassWithStudents,
            ): Boolean {
                return oldItem.classEntity.classId == newItem.classEntity.classId
            }

            override fun areContentsTheSame(
                oldItem: ClassWithStudents,
                newItem: ClassWithStudents,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }


    inner class ClassViewHolder(private val binding: ItemClassBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ClassWithStudents) {

            binding.apply {
                tvClassName.text = item.classEntity.className
                tvSection.text = " (${item.classEntity.sectionName})"
                tvTeacherName.text = item.classEntity.teacher

                tvStartSession.text = item.classEntity.startDate.toFormattedDate("dd-MM-yyyy")
                tvEndSession.text = item.classEntity.endDate.toFormattedDate("dd-MM-yyyy")

                tvTotalStudents.text = item.students.size.toString()

                ivStudents.setOnClickListener { onStudentsClick(item.classEntity) }
                ivAttendance.setOnClickListener { onAttendanceClick(item.classEntity) }
                ivReport.setOnClickListener { onReportClick(item.classEntity) }
                ivMore.setOnClickListener { view ->
                    onMoreClick(view, item.classEntity)
                }

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        return ClassViewHolder(
            ItemClassBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
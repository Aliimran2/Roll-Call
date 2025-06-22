package com.miassolutions.rollcall.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.databinding.FragmentListClassBinding
import com.miassolutions.rollcall.databinding.ItemClassBinding

class ClassListAdapter(
    private val onNavigationClick: (ClassEntity) -> Unit,
) : ListAdapter<ClassEntity, ClassListAdapter.ClassViewHolder>(ClassDiffUtil) {


    companion object {
        val ClassDiffUtil = object : DiffUtil.ItemCallback<ClassEntity>() {
            override fun areItemsTheSame(oldItem: ClassEntity, newItem: ClassEntity): Boolean {
                return oldItem.classId == newItem.classId
            }

            override fun areContentsTheSame(oldItem: ClassEntity, newItem: ClassEntity): Boolean {
                return oldItem == newItem
            }
        }
    }


    inner class ClassViewHolder(private val binding: ItemClassBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ClassEntity) {

            binding.apply {
                tvClassName.text = item.className
                tvTeacherName.text = item.teacher

                root.setOnClickListener { onNavigationClick(item) }

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
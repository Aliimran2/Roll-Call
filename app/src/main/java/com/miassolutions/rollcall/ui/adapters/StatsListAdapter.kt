package com.miassolutions.rollcall.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miassolutions.rollcall.databinding.ItemStatsBinding
import com.miassolutions.rollcall.ui.model.StatsUiModel
import com.miassolutions.rollcall.extenstions.toFormattedDate

class StatsListAdapter(
    private val deleteAction: (Long) -> Unit,
    private val onEditAttendanceAction: (Long) -> Unit,
    private val onReportAttendanceAction: (Long) -> Unit,
) : ListAdapter<StatsUiModel, StatsListAdapter.StatsViewHolder>(StatsDiffUtil()) {

    inner class StatsViewHolder(private val binding: ItemStatsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StatsUiModel) {
            binding.apply {
                tvDate.text = item.date.toFormattedDate()
                tvPresent.text = "${item.presentCount}/${item.totalCount}"
                tvPercent.text = "${item.percent}%"

                btnDelete.setOnClickListener {
                    deleteAction(item.date)
                }

                btnEditAttendance.setOnClickListener {
                    onEditAttendanceAction(item.date)
                }

                btnReportAttendance.setOnClickListener {
                    onReportAttendanceAction(item.date)
                }


            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder {
        return StatsViewHolder(
            ItemStatsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


class StatsDiffUtil : DiffUtil.ItemCallback<StatsUiModel>() {
    override fun areItemsTheSame(oldItem: StatsUiModel, newItem: StatsUiModel): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: StatsUiModel, newItem: StatsUiModel): Boolean {
        return oldItem == newItem
    }
}
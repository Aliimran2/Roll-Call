package com.miassolutions.rollcall.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miassolutions.rollcall.ui.model.StatsUiModel
import com.miassolutions.rollcall.databinding.ItemStatsBinding

class StatsListAdapter : ListAdapter<StatsUiModel, StatsListAdapter.StatsViewHolder>(StatsDiffUtil()) {

    class StatsViewHolder(private val binding: ItemStatsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StatsUiModel) {
            binding.apply {
                tvDate.text = item.date
                tvPresent.text = "${item.presentCount}/${item.totalCount}"
                tvPercent.text = "${item.percent}%"

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
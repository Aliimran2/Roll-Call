package com.miassolutions.rollcall.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miassolutions.rollcall.data.entities.Stats
import com.miassolutions.rollcall.databinding.ItemStatsBinding

class StatsListAdapter : ListAdapter<Stats, StatsListAdapter.StatsViewHolder>(StatsDiffUtil()) {

    class StatsViewHolder(private val binding: ItemStatsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Stats) {
            binding.apply {
                tvDate.text = item.date
                tvPresent.text = "${item.present}/${item.total}"

                val percentage = item.present / item.total * 100

                tvPercent.text = "$percentage%"
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
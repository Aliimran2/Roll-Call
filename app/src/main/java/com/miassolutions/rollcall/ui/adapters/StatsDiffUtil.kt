package com.miassolutions.rollcall.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.miassolutions.rollcall.ui.model.StatsUiModel

class StatsDiffUtil : DiffUtil.ItemCallback<StatsUiModel>() {
    override fun areItemsTheSame(oldItem: StatsUiModel, newItem: StatsUiModel): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: StatsUiModel, newItem: StatsUiModel): Boolean {
        return oldItem == newItem
    }
}
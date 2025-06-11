package com.miassolutions.rollcall.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.miassolutions.rollcall.data.entities.Stats

class StatsDiffUtil : DiffUtil.ItemCallback<Stats>() {
    override fun areItemsTheSame(oldItem: Stats, newItem: Stats): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: Stats, newItem: Stats): Boolean {
        return oldItem == newItem
    }
}
package com.miassolutions.rollcall.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miassolutions.rollcall.databinding.ItemCardBinding



data class Dashboard(val image: Int, val title: String)

class DashboardAdapter(
    private val dashboardItems: List<Dashboard>,
    private val onItemClick: (Dashboard) -> Unit
) : RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>() {

    inner class DashboardViewHolder(private val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Dashboard) {
            binding.apply {
                ivCard.setImageResource(item.image)
                tvCard.text = item.title



                root.setOnClickListener { onItemClick(item) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        return DashboardViewHolder(
            ItemCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dashboardItems.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val currentItem = dashboardItems[position]
        holder.bind(currentItem)


    }
}
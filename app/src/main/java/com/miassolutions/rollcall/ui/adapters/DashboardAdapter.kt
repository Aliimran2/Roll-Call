package com.miassolutions.rollcall.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.miassolutions.rollcall.databinding.ItemCardBinding
import com.miassolutions.rollcall.databinding.ItemTopCardBinding
import com.miassolutions.rollcall.ui.dataclasses.CommonListItem
import com.miassolutions.rollcall.ui.dataclasses.Dashboard
import com.miassolutions.rollcall.ui.dataclasses.TopCard
import java.lang.IllegalStateException


class DashboardAdapter(
    private val items: List<CommonListItem>,
    private val onItemClick: (CommonListItem) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {


    companion object {
        const val VIEW_TYPE_DASHBOARD = 1
        const val VIEW_TYPE_TOP_CARD = 2
    }

    inner class DashboardViewHolder(
        private val binding: ItemCardBinding,
        private val clickListener: (CommonListItem) -> Unit
    ) : ViewHolder(binding.root) {
        fun bind(item: Dashboard) {
            binding.apply {
                ivCard.setImageResource(item.image)
                tvCard.text = item.title
                root.setOnClickListener { clickListener(item) }
            }
        }
    }

    inner class TopCardViewHolder(
        private val binding: ItemTopCardBinding,
        private val clickListener: ((CommonListItem) -> Unit)? = null
    ) :
        ViewHolder(binding.root) {
        fun bind(item: TopCard) {
            binding.apply {
                tvTitle.text = item.title
                tvSubtitle.text = item.subTitle

                clickListener?.let {
                    root.setOnClickListener { it(item) }
                } ?: run {
                    root.isClickable = false
                    root.isFocusable = false
                }


            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Dashboard -> VIEW_TYPE_DASHBOARD
            is TopCard -> VIEW_TYPE_TOP_CARD
            else -> throw IllegalArgumentException("Unknown ViewType")
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder { // Return RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_DASHBOARD -> {
                val binding = ItemCardBinding.inflate(inflater, parent, false)
                DashboardViewHolder(binding, onItemClick) // Pass onItemClick here
            }

            VIEW_TYPE_TOP_CARD -> {
                val binding = ItemTopCardBinding.inflate(inflater, parent, false)
                // If TopCard should be clickable:
//                TopCardViewHolder(binding, onItemClick)
                // If TopCard should NOT be clickable:
                TopCardViewHolder(binding, null) // Pass null for the click listener
            }

            else -> throw IllegalArgumentException("Unknown ViewHolder type for viewType: $viewType") // Add viewType for debugging
        }
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = items[position]
        when (holder) {
            is DashboardViewHolder -> {
                if (currentItem is Dashboard) {
                    holder.bind(currentItem)
                } else {
                    throw IllegalStateException("Expected Dashboard")
                }
            }

            is TopCardViewHolder -> {
                if (currentItem is TopCard) {
                    holder.bind(currentItem)
                } else {
                    throw IllegalStateException("Expected TopCard")
                }
            }
        }
    }
}



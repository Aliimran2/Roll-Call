package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentDashboardBinding

import com.miassolutions.rollcall.ui.adapters.DashboardAdapter
import com.miassolutions.rollcall.ui.dataclasses.CommonListItem
import com.miassolutions.rollcall.ui.dataclasses.Dashboard
import com.miassolutions.rollcall.ui.dataclasses.TopCard
import com.miassolutions.rollcall.utils.showToast


class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var topCardAdapter: DashboardAdapter
    private lateinit var dashboardGridAdapter: DashboardAdapter

    private val topCardItems = mutableListOf<CommonListItem>()
    private val dashboardItems = mutableListOf<CommonListItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        setupDashboardItems()
        setupRecyclerView()

    }

    private fun setupDashboardItems() {

        topCardItems.apply {
            add(TopCard("11 June 2025", "Date"))
            add(TopCard("12:08 PM", "Time"))
            // Add more TopCard items if needed for horizontal scrolling
            add(TopCard("Weather", "Sunny"))
            add(TopCard("Location", "Lahore"))
        }

        dashboardItems.apply {
            add(Dashboard(R.drawable.ic_hand, "Attendance"))
            add(Dashboard(R.drawable.ic_students, "Students"))
            add(Dashboard(R.drawable.ic_stats, "History"))
            add(Dashboard(R.drawable.ic_settings, "Settings"))

        }


    }

    private fun setupRecyclerView() {
        // 1. Setup the Top Cards RecyclerView (horizontal)
        topCardAdapter = DashboardAdapter(topCardItems) { clickedItem ->
            // This lambda will ONLY be triggered by clickable items.
            // Since TopCardViewHolder is set to null click listener, this won't be called for TopCards.
            // If you change TopCardViewHolder to be clickable, this will handle it.
            if (clickedItem is TopCard) {
                showToast("Top Card clicked: ${clickedItem.title} (still unexpected if non-clickable)")
            }
        }
        binding.rvTopCard.apply { // Reference the new ID

            adapter = topCardAdapter
            // You might want to add some item decoration for spacing in horizontal lists
//             addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL))
        }


        // 2. Setup the Dashboard Grid RecyclerView
        dashboardGridAdapter = DashboardAdapter(dashboardItems) { clickedItem ->
            when (clickedItem) {
                is Dashboard -> {
                    when (clickedItem.title) {
                        "Attendance" -> showToast("Attendance is clicked")
                        "Students" -> showToast("Students is clicked")
                        "History" -> showToast("History is clicked")
                        "Settings" -> showToast("Settings is clicked")
                    }

                    // Add navigation logic here, e.g.:
                    // findNavController().navigate(R.id.action_dashboard_to_detailFragment, bundleOf("itemTitle" to clickedItem.title))
                }
                // No need for TopCard 'is' check here, as this adapter only receives Dashboard items.
            }
        }
        binding.rvDashboard.apply { // Reference the existing ID

            adapter = dashboardGridAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentDashboardBinding

import com.miassolutions.rollcall.ui.adapters.DashboardAdapter
import com.miassolutions.rollcall.ui.model.CommonListItem
import com.miassolutions.rollcall.ui.model.Dashboard
import com.miassolutions.rollcall.ui.model.TopCard
import com.miassolutions.rollcall.utils.showLongToast
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

        topCardItems.clear()
        dashboardItems.clear()

        topCardItems.apply {
            add(TopCard("11 June 2025", "Date"))
            add(TopCard("12:08 PM", "Time"))
//            // Add more TopCard items if needed for horizontal scrolling
//            add(TopCard("Weather", "Sunny"))
//            add(TopCard("Location", "Lahore"))
        }

        dashboardItems.apply {
            add(Dashboard(R.drawable.ic_attendances, "Attendance"))
            add(Dashboard(R.drawable.ic_students_m, "Students"))
            add(Dashboard(R.drawable.ic_person, "My Profile"))
            add(Dashboard(R.drawable.ic_settings, "Settings"))

        }


    }

    private fun setupRecyclerView() {

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
                        "Attendance" -> {
                            val action =
                                DashboardFragmentDirections.actionDashboardFragmentToStatsFragment()
                            findNavController().navigate(action)
                        }

                        "Students" -> {
                            val action =
                                DashboardFragmentDirections.actionDashboardFragmentToStudentsFragment()
                            findNavController().navigate(action)
                        }

                        "My Profile" -> {
                            showLongToast("Implement bottom fragment for input profile")
                        }

                        "Settings" -> {
                            val action =
                                DashboardFragmentDirections.actionDashboardFragmentToSettingsFragment()
                            findNavController().navigate(action)
                        }
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
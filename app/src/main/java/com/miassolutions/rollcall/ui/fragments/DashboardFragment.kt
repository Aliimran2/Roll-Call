package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentDashboardBinding

import com.miassolutions.rollcall.ui.adapters.DashboardAdapter
import com.miassolutions.rollcall.ui.model.CommonListItem
import com.miassolutions.rollcall.ui.model.Dashboard
import com.miassolutions.rollcall.ui.model.TopCard
import com.miassolutions.rollcall.ui.viewmodels.SettingsViewModel
import com.miassolutions.rollcall.utils.getCurrentDateAndTime
import com.miassolutions.rollcall.utils.toFormattedDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!


    private val settingsViewModel by viewModels<SettingsViewModel>()

    private lateinit var dashboardGridAdapter: DashboardAdapter


    private val dashboardItems = mutableListOf<CommonListItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        setupDashboardItems()
        setupRecyclerView()
        setDateCard()
        observeViewModel()

    }

    private fun setDateCard() {
        binding.dateCard.apply {
            tvTitle.text = getCurrentDateAndTime().toFormattedDate("EEEE\ndd.MM.yyyy")
            tvSubtitle.text = "Date"

        }
    }

    private fun observeViewModel() {


        settingsViewModel.userName.observe(viewLifecycleOwner) {
            it?.let {

                binding.userProfileCard.tvTitle.text = "Welcome!\n$it"
            }
        }

        settingsViewModel.instituteName.observe(viewLifecycleOwner) {
            it?.let {

                binding.userProfileCard.tvSubtitle.text = it
            }

        }
    }


    private fun setupDashboardItems() {
        dashboardItems.clear()
//        topCardItems.apply {
//            add(TopCard(getCurrentDateAndTime().toFormattedDate("EEEE\ndd/MM/yyyy"), "Date"))
//            add(TopCard(userName ?: "Set user name", userInstitute ?: "Set institute name"))
//        }

        dashboardItems.apply {
            add(Dashboard(R.drawable.ic_attendances, "Attendance"))
            add(Dashboard(R.drawable.ic_students_m, "Students"))
            add(Dashboard(R.drawable.ic_person, "My Profile"))
            add(Dashboard(R.drawable.ic_settings, "Settings"))

        }
    }

    private fun setupRecyclerView() {

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

                            val action =
                                DashboardFragmentDirections.actionDashboardFragmentToUserProfileFragment()
                            findNavController().navigate(action)


                        }

                        "Settings" -> {
                            val action =
                                DashboardFragmentDirections.actionDashboardFragmentToSettingsFragment()
                            findNavController().navigate(action)
                        }
                    }

                }

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
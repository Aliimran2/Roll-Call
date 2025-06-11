package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentDashboardBinding
import com.miassolutions.rollcall.ui.adapters.Dashboard
import com.miassolutions.rollcall.ui.adapters.DashboardAdapter
import com.miassolutions.rollcall.utils.showToast


class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DashboardAdapter
    private val dashBoardList = mutableListOf<Dashboard>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)




        setupDashboardItems()
        setupRecyclerView()


    }

    private fun setupDashboardItems() {
        dashBoardList.add(Dashboard(R.drawable.ic_hand, "Attendance"))
        dashBoardList.add(Dashboard(R.drawable.ic_students, "Students"))
        dashBoardList.add(Dashboard(R.drawable.ic_stats, "History"))
        dashBoardList.add(Dashboard(R.drawable.ic_settings, "Settings"))
    }

    private fun setupRecyclerView() {


        adapter = DashboardAdapter(dashBoardList) { item ->
            when (item.title) {
                "Attendance" -> showToast("Attendance")
                "Students" -> showToast("Students")
                "History" -> showToast("History")
                "Settings" -> showToast("Settings")
            }
        }

        binding.rvDashboard.adapter = adapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentStatsBinding
import com.miassolutions.rollcall.ui.adapters.StatsListAdapter
import com.miassolutions.rollcall.ui.viewmodels.StatsViewModel
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showMaterialDatePicker
import com.miassolutions.rollcall.extenstions.showSnackbar
import com.miassolutions.rollcall.extenstions.toFormattedDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats) {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<StatsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStatsBinding.bind(view)

        val adapter = StatsListAdapter(::deleteAttendance, ::editAttendance, ::reportAttendance)

        collectLatestFlow {
            viewModel.filteredSummary.collectLatest {
                adapter.submitList(it)
            }
        }

        binding.rvStats.adapter = adapter

        binding.btnTakeAtt.setOnClickListener {
            val action = StatsFragmentDirections
                .actionStatsFragmentToAttendanceFragment(
                    attendanceMode = "add",
                    selectedDate = -1L
                )
            findNavController().navigate(action)
        }

        binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnSearchAttendance -> {
                        showMaterialDatePicker(
                            "Select Attendance Date",
                            onDateSelected = {
                                binding.btnSearchAttendance.text = it.toFormattedDate()
                                viewModel.setDate(it)
                            }
                        )
                    }

                    R.id.btnAllAttendances -> {
                        viewModel.setDate(0L)
                        binding.btnSearchAttendance.text = "Select Date"
                    }
                }
            }
        }


    }

    private fun editAttendance(date: Long) {
        val action = StatsFragmentDirections.actionStatsFragmentToAttendanceFragment(
            attendanceMode = "update",
            selectedDate = date
        )
        findNavController().navigate(action)
    }

    private fun reportAttendance(date: Long) {
        val action = StatsFragmentDirections.actionStatsFragmentToAttendanceFragment("report", date)
        findNavController().navigate(action)
    }

    private fun deleteAttendance(date: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Confirmation!!")
            .setMessage("Are you suer?")
            .setPositiveButton("Yes, Delete") { _, _ ->
                viewModel.deleteAttendance(date)
                showSnackbar("Attendance record deleted for ${date.toFormattedDate()}")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
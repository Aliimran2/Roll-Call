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
import com.miassolutions.rollcall.extenstions.showToast
import com.miassolutions.rollcall.extenstions.toFormattedDate
import com.miassolutions.rollcall.utils.toLocalDate
import com.miassolutions.rollcall.utils.toMillis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_stats) {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<StatsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStatsBinding.bind(view)

        val adapter = StatsListAdapter(::deleteAttendance, ::editAttendance, ::reportAttendance)
        binding.rvStats.adapter = adapter

        collectLatestFlow {
            viewModel.filteredSummary.collectLatest {
                adapter.submitList(it)
            }
        }

        binding.btnTakeAtt.setOnClickListener {
            val action = StatsFragmentDirections
                .actionStatsFragmentToAttendanceFragment(
                    attendanceMode = "add",
                    selectedDate = -1L // keep as -1L for navigation args, convert in fragment
                )
            findNavController().navigate(action)
        }

        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnAllAttendances -> {
                        viewModel.setDate(null) // null to indicate all
                        binding.btnSearchAttendance.text = "Select Date"
                    }

                    R.id.btnSearchAttendance -> {
                        showMaterialDatePicker(
                            title = "Select Attendance Date",
                            onDateSelected = { millis ->
                                val date = millis.toLocalDate()
                                binding.btnSearchAttendance.text = date.toMillis().toFormattedDate()
                                viewModel.setDate(date)
                                binding.toggleGroup.clearChecked()
                                binding.btnSearchAttendance.isChecked = false
                            }
                        )
                    }
                }
            }
        }
    }

    private fun editAttendance(date: LocalDate) {
        val action = StatsFragmentDirections.actionStatsFragmentToAttendanceFragment(
            attendanceMode = "update",
            selectedDate = date.toMillis()
        )
        findNavController().navigate(action)
    }

    private fun reportAttendance(date: LocalDate) {
        val action = StatsFragmentDirections.actionStatsFragmentToAttendanceFragment(
            attendanceMode = "report",
            selectedDate = date.toMillis()
        )
        findNavController().navigate(action)
    }

    private fun deleteAttendance(date: LocalDate) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Confirmation!!")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes, Delete") { _, _ ->
                viewModel.deleteAttendance(date)
                showSnackbar("Attendance record deleted for ${date}")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

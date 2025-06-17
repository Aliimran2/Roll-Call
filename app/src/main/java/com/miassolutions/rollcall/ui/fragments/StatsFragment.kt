package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentStatsBinding
import com.miassolutions.rollcall.ui.adapters.StatsListAdapter
import com.miassolutions.rollcall.ui.viewmodels.StatsViewModel
import com.miassolutions.rollcall.utils.collectLatestFlow
import com.miassolutions.rollcall.utils.showSnackbar
import com.miassolutions.rollcall.utils.showToast
import com.miassolutions.rollcall.utils.toFormattedDate
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
            viewModel.attendanceSummary.collectLatest {
                adapter.submitList(it)
            }
        }

        binding.rvStats.adapter = adapter





        binding.btnTakeAtt.setOnClickListener {
            val action =
                StatsFragmentDirections.actionStatsFragmentToAttendanceFragment()
            findNavController().navigate(action)
        }


    }

    private fun editAttendance(date: Long) {
        StatsFragmentDirections.actionStatsFragmentToAttendanceFragment()
    }

    private fun reportAttendance(date: Long) {
        showToast("Reporting...")
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


//    private fun menuProvider() {
//        val menuHost = requireActivity()
//        menuHost.addMenuProvider(
//            object : MenuProvider {
//                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                    menuInflater.inflate(R.menu.menu_attendance, menu)
//                }
//
//                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                    return when (menuItem.itemId) {
//                        R.id.action_add_att -> {
//                            val action =
//                                StatsFragmentDirections.actionStatsFragmentToAttendanceFragment()
//                            findNavController().navigate(action)
////                            true
//                        }
//
//                        else -> false
//                    }
//                }
//            },
//            viewLifecycleOwner,
//            Lifecycle.State.RESUMED
//        )
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
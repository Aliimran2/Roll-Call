package com.miassolutions.rollcall.ui.screens.attandancescreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentAttendanceListBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.ui.viewmodels.StatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttendanceListFragment : Fragment(R.layout.fragment_attendance_list) {

    private var _binding: FragmentAttendanceListBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<StatsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAttendanceListBinding.bind(view)

//        val adapter = StatsListAdapter(::deleteAttendance, ::editAttendance, ::reportAttendance)

        collectLatestFlow {
//            viewModel.attendanceSummary.collectLatest {
//                adapter.submitList(it)
//            }
        }

//        binding.rvStats.adapter = adapter





        binding.btnTakeAtt.setOnClickListener {
            val action = AttendanceListFragmentDirections
                .actionStatsFragmentToAttendanceFragment(
                    attendanceMode = "add",
                    selectedDate = -1L
                )
            findNavController().navigate(action)
        }


    }

    private fun editAttendance(date: Long) {
        val action = AttendanceListFragmentDirections.actionStatsFragmentToAttendanceFragment(
            attendanceMode = "update",
            selectedDate = date
        )
        findNavController().navigate(action)
    }

    private fun reportAttendance(date: Long) {
        val action = AttendanceListFragmentDirections.actionStatsFragmentToAttendanceFragment("report", date)
        findNavController().navigate(action)
    }

//    private fun deleteAttendance(date: Long) {
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle("Delete Confirmation!!")
//            .setMessage("Are you suer?")
//            .setPositiveButton("Yes, Delete") { _, _ ->
//                viewModel.deleteAttendance(date)
//                showSnackbar("Attendance record deleted for ${date.toFormattedDate()}")
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }


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
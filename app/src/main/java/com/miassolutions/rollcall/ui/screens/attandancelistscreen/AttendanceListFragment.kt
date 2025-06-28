package com.miassolutions.rollcall.ui.screens.attandancelistscreen

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentAttendanceListBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showSnackbar
import com.miassolutions.rollcall.extenstions.toFormattedDate
import com.miassolutions.rollcall.ui.adapters.StatsListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AttendanceListFragment : Fragment(R.layout.fragment_attendance_list) {

    private var _binding: FragmentAttendanceListBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AttendanceStatsViewModel>()

    private val args by navArgs<AttendanceListFragmentArgs>()

    private val adapter by lazy {
        StatsListAdapter(
            ::deleteAttendance,
            ::editAttendance,
            ::reportAttendance
        )
    }

    private lateinit var toolbar: MaterialToolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAttendanceListBinding.bind(view)


        viewModel.setClassId(args.classId)

        toolbar = (activity as AppCompatActivity).findViewById(R.id.toolbar)
        toolbar.subtitle = null

        observeUiState()
        observeUIEvent()
        setupRecyclerview()


        binding.btnTakeAtt.setOnClickListener {
            val action = AttendanceListFragmentDirections
                .actionStatsFragmentToAttendanceFragment(
                    attendanceMode = "add",
                    selectedDate = -1L
                )
            findNavController().navigate(action)
        }


    }

    private fun setupRecyclerview() {
        binding.rvStats.adapter = adapter
    }

    private fun observeUiState() {
        collectLatestFlow {
            viewModel.uiState.collectLatest { state ->
                adapter.submitList(state.attendanceStats)

            }
        }
    }

    private fun observeUIEvent() {
        collectLatestFlow {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is AttendanceStatsUiEvent.NavToAddEditAttendance -> {


                    }
                    is AttendanceStatsUiEvent.NavToReportAttendance -> {}
                    is AttendanceStatsUiEvent.ShowDeleteConfirmation -> {}
                    is AttendanceStatsUiEvent.ShowSnackbar -> {
                        showSnackbar(event.message)
                    }
                }
            }
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
        val action =
            AttendanceListFragmentDirections.actionStatsFragmentToAttendanceFragment("report", date)
        findNavController().navigate(action)
    }

    private fun deleteAttendance(date: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Confirmation!!")
            .setMessage("Are you suer?")
            .setPositiveButton("Yes, Delete") { _, _ ->
//                viewModel.deleteAttendance(date)
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
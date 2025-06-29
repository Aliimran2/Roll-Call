package com.miassolutions.rollcall.ui.screens.attandancelistscreen

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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

    private lateinit var toolbar: MaterialToolbar

    private val adapter by lazy {
        StatsListAdapter(
            deleteAction = { date -> confirmDelete(date) },
            onEditAttendanceAction = { date -> viewModel.onEditClick(date) },
            onReportAttendanceAction = { date -> viewModel.onReportClick(date) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAttendanceListBinding.bind(view)

        viewModel.setClassId(args.classId)
        setupToolbar()
        setupRecyclerView()
        observeUiState()
        observeUiEvent()

        binding.btnTakeAtt.setOnClickListener {
            val action = AttendanceListFragmentDirections
                .actionStatsFragmentToAttendanceFragment(
                    attendanceMode = "add",
                    selectedDate = -1L,
                    classId = args.classId,
                    className = args.className,
                    sectionName = args.sectionName,
                )
            findNavController().navigate(action)
        }
    }

    private fun setupToolbar(){

        toolbar = (activity as AppCompatActivity).findViewById(R.id.toolbar)
        toolbar.subtitle = null
    }

    private fun setupRecyclerView() {
        binding.rvStats.adapter = adapter
    }

    private fun observeUiState() {
        collectLatestFlow {
            viewModel.uiState.collectLatest { state ->
                adapter.submitList(state.attendanceStats)
//                binding.progressBar.isVisible = state.isLoading
//                binding.tvEmpty.isVisible = !state.isLoading && state.attendanceStats.isEmpty()
            }
        }
    }

    private fun observeUiEvent() {
        collectLatestFlow {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is AttendanceStatsUiEvent.NavToAddEditAttendance -> {

                            val action = AttendanceListFragmentDirections
                                .actionStatsFragmentToAttendanceFragment(
                                    attendanceMode = "update",
                                    selectedDate = event.date,
                                    classId = args.classId,
                                    className = args.className,
                                    sectionName = args.sectionName
                                )
                            findNavController().navigate(action)


                    }

                    is AttendanceStatsUiEvent.NavToReportAttendance -> {
                        val action = AttendanceListFragmentDirections
                            .actionStatsFragmentToAttendanceFragment(
                                attendanceMode = "report",
                                selectedDate = event.date,
                                classId = args.classId,
                                className = args.className,
                                sectionName = args.sectionName
                            )
                        findNavController().navigate(action)
                    }



                    is AttendanceStatsUiEvent.ShowSnackbar -> {
                        showSnackbar(event.message)
                    }

                }
            }
        }
    }

    private fun confirmDelete(date: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Confirmation!")
            .setMessage("Are you sure you want to delete attendance for ${date.toFormattedDate()}?")
            .setPositiveButton("Yes, Delete") { _, _ ->
                viewModel.onDeleteClick(date)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

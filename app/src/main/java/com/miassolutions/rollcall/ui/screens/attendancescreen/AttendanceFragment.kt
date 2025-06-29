package com.miassolutions.rollcall.ui.screens.attendancescreen

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.common.AttendanceFilter
import com.miassolutions.rollcall.databinding.FragmentAttendanceBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.hide
import com.miassolutions.rollcall.extenstions.show
import com.miassolutions.rollcall.extenstions.showMaterialDatePicker
import com.miassolutions.rollcall.extenstions.toFormattedDate
import com.miassolutions.rollcall.ui.attendance.AttendanceUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AttendanceFragment : Fragment(R.layout.fragment_attendance) {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AttendanceAdapter
    private val viewModel by viewModels<AttendanceViewModel>()

    private val navArgs by navArgs<AttendanceFragmentArgs>()
    private var attendanceMode = "add"
    private var selectedDate: Long = -1L


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAttendanceBinding.bind(view)


        attendanceMode = navArgs.attendanceMode
        selectedDate = navArgs.selectedDate
        viewModel.setClassId(navArgs.classId)

        if (attendanceMode == "Add") {
            setupUI(true)

        } else if (attendanceMode == "Update") {
            setupUI(false)
        }

        setupRecyclerView()
        observeViewModel()
        setupListeners()


    }

    private fun setupListeners() {
        binding.apply {
            etDatePicker.setOnClickListener {
                showMaterialDatePicker("Select Date") {
                    viewModel.setDate(it)
                    etDatePicker.setText(it.toFormattedDate())
                }
            }

            attendanceToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    val filter = when (checkedId) {
                        R.id.btnAll -> AttendanceFilter.ALL
                        R.id.btnPresent -> AttendanceFilter.PRESENT
                        R.id.btnAbsent -> AttendanceFilter.ABSENT
                        else -> AttendanceFilter.ALL
                    }

                    viewModel.setFilter(filter)
                } else {
                    viewModel.saveAttendance { success ->
                        if (success){
                            showSuccess("Attendance saved")
                        } else {
                            showError("Failed to save attendance")
                        }
                    }
                }
            }

            saveBtn.setOnClickListener {
                if (selectedDate != 0L){
                    viewModel.updateAttendanceForDate(navArgs.classId, viewModel.date.value){success ->
                        if (success){
                            showSuccess("Attendance Updated")
                        } else {
                            showError("Failed to update")
                        }
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        collectLatestFlow {
            launch {
                viewModel.filteredAttendanceUI.collectLatest {
                    adapter.submitList(it)
                }
            }
            launch {
                viewModel.date.collectLatest { dateInMillis ->
                    binding.etDatePicker.setText(dateInMillis.toFormattedDate())
                }
            }


        }
    }

    private fun setupUI(isReadOnly: Boolean) {
        binding.apply {
            if (isReadOnly) attendanceToggleGroup.show() else attendanceToggleGroup.hide()
        }
    }


    private fun setupRecyclerView() {
        adapter = AttendanceAdapter(readOnly = false) { student, status ->
            viewModel.updateAttendanceStatus(
                student,
                status
            )
        }
        binding.rvAttendance.adapter = adapter
    }


    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.green))
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

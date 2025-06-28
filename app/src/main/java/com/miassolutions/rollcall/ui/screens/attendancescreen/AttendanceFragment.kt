package com.miassolutions.rollcall.ui.screens.attendancescreen

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.common.AttendanceFilter
import com.miassolutions.rollcall.common.Constants
import com.miassolutions.rollcall.databinding.FragmentAttendanceBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.hide
import com.miassolutions.rollcall.extenstions.setToolbarTitle
import com.miassolutions.rollcall.extenstions.show
import com.miassolutions.rollcall.extenstions.showMaterialDatePicker
import com.miassolutions.rollcall.extenstions.toFormattedDate
import com.miassolutions.rollcall.ui.attendance.AttendanceAdapter
import com.miassolutions.rollcall.ui.attendance.AttendanceUiEvent
import com.miassolutions.rollcall.ui.attendance.AttendanceUiState

import com.miassolutions.rollcall.ui.attendance.AttendanceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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

        observeUiState()
        setupRecyclerView()
        setupViews()

    }

    private fun setupViews() {
        binding.attendanceToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnAll -> viewModel.onEvent(AttendanceUiEvent.UpdateFilter(AttendanceFilter.ALL))

                    R.id.btnAbsent -> viewModel.onEvent(
                        AttendanceUiEvent.UpdateFilter(
                            AttendanceFilter.ABSENT
                        )
                    )

                    R.id.btnPresent -> viewModel.onEvent(
                        AttendanceUiEvent.UpdateFilter(
                            AttendanceFilter.PRESENT
                        )
                    )
                }
            }
        }
        binding.etSearch.doAfterTextChanged { text ->
            viewModel.onEvent(AttendanceUiEvent.UpdateSearchQuery(text.toString()))
        }

        binding.etDatePicker.setOnClickListener {
            showMaterialDatePicker("Select Date"){
                viewModel.setDate(it)
                binding.etDatePicker.setText(it.toFormattedDate())
            }
        }


        // Setup save button (hide in report mode)
        binding.saveBtn.apply {
            if (attendanceMode == "report") {
                hide()
            } else {
                setOnClickListener {
                    viewModel.onEvent(AttendanceUiEvent.SaveAttendance)
                }
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.green))
            .show()
    }

    private fun observeUiState() {
        collectLatestFlow {
            viewModel.filteredAttendance.collectLatest { students ->
                adapter.submitList(students)
            }

            viewModel.uiState.collectLatest { state ->

                if (state.isLoading || state.isSaving) {
                    binding.progressBar.show()
                } else {
                    binding.progressBar.hide()
                }

                updateCountCards(state.counts)



                state.selectedDate?.let { date ->
                    if (binding.etDatePicker.text.toString() != date.toFormattedDate()) {
                        binding.etDatePicker.setText(date.toFormattedDate())
                    }
                }

                state.error?.let { eror ->
                    showError(eror)

                }

                if (state.saveSuccess){
                    showSuccess("Attendance saved successfully")
                    viewModel.onEvent(AttendanceUiEvent.SaveAttendance)
                }

            }
        }
    }

    private fun updateCountCards(counts: AttendanceUiState.AttendanceCounts) {
        binding.apply {
            totalCard.tvCountTitle.text = "Total"
            presentCard.tvCountTitle.text = "Present"
            absentCard.tvCountTitle.text = "Absent"
            totalCard.tvCount.text = counts.total.toString()
            presentCard.tvCount.text = counts.present.toString()
            absentCard.tvCount.text = counts.absent.toString()
        }
    }


    private fun setupRecyclerView() {
        val readOnly = attendanceMode == "report"

        adapter = AttendanceAdapter(readOnly) { student, newStatus ->
            viewModel.onEvent(AttendanceUiEvent.UpdateStatus(student.studentId, newStatus))
        }
        binding.rvAttendance.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.Attendance
import com.miassolutions.rollcall.data.entities.MarkAttendanceUiModel
import com.miassolutions.rollcall.databinding.FragmentAttendanceBinding
import com.miassolutions.rollcall.ui.adapters.AttendanceAdapter
import com.miassolutions.rollcall.ui.viewmodels.AttendanceViewModel
import com.miassolutions.rollcall.utils.collectLatestFlow
import com.miassolutions.rollcall.utils.getCurrentDate
import com.miassolutions.rollcall.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AttendanceFragment : Fragment(R.layout.fragment_attendance) {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AttendanceAdapter
    private val viewModel by viewModels<AttendanceViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAttendanceBinding.bind(view)

        setupRecyclerView()
        viewModel.setDate(getCurrentDate())

        collectLatestFlow {
            launch {
                viewModel.totalCount.collectLatest {
                    binding.totalCard.tvCount.text = it.toString()
                }
            }
            launch {
                viewModel.presentCount.collectLatest {
                    binding.presentCard.tvCount.text = it.toString()
                    binding.presentCard.tvCount.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green_present
                        )
                    )
                }
            }
            launch {
                viewModel.absentCount.collectLatest {
                    binding.absentCard.tvCount.text = it.toString()
                    binding.absentCard.tvCount.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red_absent
                        )
                    )
                }
            }
            launch {
                viewModel.uiState.collectLatest { adapter.submitList(it) }
            }
        }

        binding.etDate.setOnClickListener{
            showDatePickerDialog()
        }

        binding.saveBtn.setOnClickListener {
            val date = getCurrentDate()
            val attendanceEntity = viewModel.uiState.value.map {
                Attendance(
                    studentId = it.studentId,
                    date = date,
                    attendanceStatus = it.attendanceStatus
                )
            }
            viewModel.saveAttendance(attendanceEntity)
            showSnackbar("Attendance Saved")
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment {selectedDate ->
            binding.etDate.setText(selectedDate)

        }
        datePicker.show(parentFragmentManager, null)
    }

    private fun setupRecyclerView() {
        adapter = AttendanceAdapter { student, newStatus ->
            viewModel.updateAttendanceStatus(student, newStatus)
        }
        binding.rvAttendance.adapter = adapter

        viewModel.studentList.observe(viewLifecycleOwner) { students ->
            val initialList = students.map {
                MarkAttendanceUiModel(
                    studentId = it.studentId,
                    studentName = it.studentName,
                    rollNumber = it.rollNumber
                )
            }
            viewModel.setInitialAttendanceList(initialList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

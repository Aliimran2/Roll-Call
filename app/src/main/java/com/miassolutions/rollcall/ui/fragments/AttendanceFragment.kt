package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import kotlin.math.abs

@AndroidEntryPoint
class AttendanceFragment : Fragment(R.layout.fragment_attendance) {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AttendanceAdapter
    private val viewModel by viewModels<AttendanceViewModel>()
    private val attendanceList = mutableListOf<MarkAttendanceUiModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAttendanceBinding.bind(view)

        setupRecyclerView()

        viewModel.setDate(getCurrentDate())

        collectLatestFlow {
            launch {

                viewModel.totalMarkedCount.collectLatest { total ->
                    binding.tvTotal.text = total.toString()
                }
            }
            launch {

                viewModel.absentCount.collectLatest { absent ->
                    binding.tvAbsent.text = absent.toString()
                }
            }

            launch {

                viewModel.presentCount.collectLatest { present ->
                    binding.tvPresent.text = present.toString()
                }
            }
        }

        binding.btnSave.setOnClickListener {
            val date = getCurrentDate()
            val attendanceEntity = attendanceList.map {
                Attendance(
                    studentId = it.studentId,
                    date = date,
                    attendanceStatus = it.attendanceStatus
                )
            }
            viewModel.saveAttendance(attendanceEntity)
            showSnackbar("Save Attendance")
        }

    }


    private fun setupRecyclerView() {
        adapter = AttendanceAdapter { student, newStatus ->
            student.attendanceStatus = newStatus
        }
        binding.rvAttendance.adapter = adapter

        viewModel.studentList.observe(viewLifecycleOwner) { students ->
            attendanceList.clear()
            attendanceList.addAll(students.map {
                MarkAttendanceUiModel(
                    studentId = it.studentId,
                    studentName = it.studentName,
                    rollNumber = it.rollNumber
                )
            })
            adapter.submitList(attendanceList.toList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
package com.miassolutions.rollcall.ui.fragments

import SundayPastDateValidator
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentAttendanceBinding
import com.miassolutions.rollcall.ui.adapters.AttendanceAdapter

import com.miassolutions.rollcall.ui.viewmodels.AttendanceViewModel
import com.miassolutions.rollcall.utils.Constants
import com.miassolutions.rollcall.utils.Constants.DATE_REQUEST_KEY
import com.miassolutions.rollcall.utils.collectLatestFlow
import com.miassolutions.rollcall.utils.showMaterialDatePicker
import com.miassolutions.rollcall.utils.showSnackbar
import com.miassolutions.rollcall.utils.toFormattedDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

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

        if (attendanceMode == "update" && selectedDate != -1L) {
            // Pre-fill date and disable picker
            binding.etDatePicker.setText(selectedDate.toFormattedDate())
            binding.etDatePicker.isEnabled = false

            // Load attendance from DB
            viewModel.setDate(selectedDate)

        }




        setupDateChangeListener()
        setupRecyclerView()
        collectFlows()
        clickListener()

    }


    private fun setupDateChangeListener() {
        parentFragmentManager.setFragmentResultListener(
            DATE_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val selectedDate = bundle.getLong(Constants.SELECTED_DATE)
            binding.etDatePicker.setText(selectedDate.toFormattedDate())
        }
    }

    private fun clickListener() {

        binding.apply {
            etDatePicker.setOnClickListener {
                showDatePicker {
                    etDatePicker.setText(it.toFormattedDate())
                }

            }

            saveBtn.setOnClickListener {
                val dateStr = binding.etDatePicker.text.toString()
                if (dateStr.isEmpty()) {
                    showSnackbar("Select date first")
                    return@setOnClickListener
                }

                val date = selectedDate.takeIf { it != -1L } ?: return@setOnClickListener // Use the selectedDate from navArgs

                if (attendanceMode == "update") {
                    viewModel.updateAttendanceForDate(date) { success ->
                        if (success) {
                            showSnackbar("Attendance updated for $dateStr")
                            findNavController().navigateUp()
                        } else {
                            showSnackbar("Failed to update attendance for $dateStr")
                        }
                    }
                } else {
                    viewModel.saveAttendance { success ->
                        if (success) {
                            showSnackbar("Attendance saved for $dateStr")
                            findNavController().navigateUp()
                        } else {
                            showSnackbar("Attendance already exists for $dateStr")
                        }
                    }
                }
            }

        }


    }



    private fun showDatePicker(onDateSelected: (Long) -> Unit) {

        if (attendanceMode == "update") return  // Prevent interaction

        val constraintsBuilder = CalendarConstraints.Builder()
            .setFirstDayOfWeek(Calendar.MONDAY)
            .setValidator(SundayPastDateValidator())

        showMaterialDatePicker(
            title = "Select Attendance Date",
            selection = MaterialDatePicker.todayInUtcMilliseconds(),
            constraints = constraintsBuilder.build(),
            ) {
            onDateSelected(it)
            viewModel.setDate(it)
        }


    }


    private fun collectFlows() {
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
                viewModel.attendanceUI.collectLatest { adapter.submitList(it) }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = AttendanceAdapter { student, newStatus ->
            viewModel.updateAttendanceStatus(student, newStatus)
        }
        binding.rvAttendance.adapter = adapter



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

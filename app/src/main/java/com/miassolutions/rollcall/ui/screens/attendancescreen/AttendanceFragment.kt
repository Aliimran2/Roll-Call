package com.miassolutions.rollcall.ui.screens.attendancescreen

import WeekendPastDateValidatorUtil
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.common.AttendanceFilter
import com.miassolutions.rollcall.common.Constants
import com.miassolutions.rollcall.common.Constants.DATE_REQUEST_KEY
import com.miassolutions.rollcall.databinding.FragmentAttendanceBinding
import com.miassolutions.rollcall.extenstions.*
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
    private var studentsCount: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAttendanceBinding.bind(view)

        attendanceMode = navArgs.attendanceMode
        selectedDate = navArgs.selectedDate

        viewModel.setClassId(navArgs.classId)

        setupModeUI()
        setupDateChangeListener()
        setupRecyclerView()
        collectFlows()
        setupClickListeners()
        setupFilterListener()
        setupSearchListener()
    }

    private fun setupModeUI() {
        binding.apply {
            when (attendanceMode) {
                "add" ->{
                    setToolbarTitle("Take Attendance")
                    val today = viewModel.date.value
                    binding.etDatePicker.setText(today.toFormattedDate())
                }

                "update" -> {
                    setToolbarTitle("Update Attendance")
                    attendanceToggleGroup.show()
                    etDatePicker.setText(selectedDate.toFormattedDate())
                    etDatePicker.isEnabled = false
                    saveBtn.text = "Update"
                    viewModel.setDate(selectedDate)
                }
                "report" -> {
                    setToolbarTitle("Report ${selectedDate.toFormattedDate()}")
                    attendanceToggleGroup.show()
                    etDatePicker.setText(selectedDate.toFormattedDate())
                    etDatePicker.isEnabled = false
                    etDatePicker.hide()
                    saveBtn.hide()
                    viewModel.setDate(selectedDate)
                }
            }
        }
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener {
            viewModel.updateSearchQuery(it.toString())
        }
    }

    private fun setupDateChangeListener() {
        parentFragmentManager.setFragmentResultListener(DATE_REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
            val date = bundle.getLong(Constants.SELECTED_DATE)
            binding.etDatePicker.setText(date.toFormattedDate())
        }
    }

    private fun setupClickListeners() = binding.apply {
        etDatePicker.setOnClickListener {
            if (attendanceMode == "update" || attendanceMode == "report") return@setOnClickListener
            showDatePicker {
                etDatePicker.setText(it.toFormattedDate())
                viewModel.setDate(it)
            }
        }

        saveBtn.setOnClickListener {
            val dateStr = etDatePicker.text.toString()
            if (dateStr.isEmpty()) return@setOnClickListener showSnackbar("Select date first")
            if (studentsCount == 0) return@setOnClickListener showSnackbar("No students for attendance")

            val date = viewModel.date.value
            when (attendanceMode) {
                "update" -> viewModel.updateAttendanceForDate(navArgs.classId, date) {
                    val msg = if (it) "Attendance updated" else "Failed to update"
                    showSnackbar("$msg for $dateStr")
                    if (it) findNavController().navigateUp()
                }
                "report" -> setupFilterListener()
                else -> viewModel.saveAttendance {
                    val msg = if (it) "Attendance saved" else "Attendance already exists"
                    showSnackbar("$msg for $dateStr")
                    if (it) findNavController().navigateUp()
                }
            }
        }
    }

    private fun setupFilterListener() {
        binding.attendanceToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            val filter = when (checkedId) {
                R.id.btnAll -> AttendanceFilter.ALL
                R.id.btnPresent -> AttendanceFilter.PRESENT
                R.id.btnAbsent -> AttendanceFilter.ABSENT
                else -> AttendanceFilter.ALL
            }
            viewModel.setFilter(filter)
        }
    }

    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val constraints = CalendarConstraints.Builder()
            .setFirstDayOfWeek(Calendar.MONDAY)
            .setValidator(WeekendPastDateValidatorUtil())
            .build()

        showMaterialDatePicker(
            title = "Select Attendance Date",
            selection = MaterialDatePicker.todayInUtcMilliseconds(),
            constraints = constraints
        ) {
            onDateSelected(it)
        }
    }

    private fun collectFlows() {
        collectLatestFlow {
            launch {
                viewModel.filteredAttendanceUI.collectLatest { adapter.submitList(it) }
            }
            launch {
                viewModel.totalCounts.collectLatest {
                    binding.totalCard.tvCount.text = it.toString()
                    studentsCount = it
                }
            }
            launch {
                viewModel.presentCount.collectLatest {
                    binding.presentCard.tvCount.text = it.toString()
                    binding.presentCard.tvCountTitle.text = "Present"
                    binding.presentCard.tvCount.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.green_present)
                    )
                }
            }
            launch {
                viewModel.absentCount.collectLatest {
                    binding.absentCard.tvCount.text = it.toString()
                    binding.absentCard.tvCountTitle.text = "Absent"
                    binding.absentCard.tvCount.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.red_absent)
                    )
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val readOnly = attendanceMode == "report"
        adapter = AttendanceAdapter(readOnly) { student, newStatus ->
            viewModel.updateAttendanceStatus(student, newStatus)
        }
        binding.rvAttendance.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

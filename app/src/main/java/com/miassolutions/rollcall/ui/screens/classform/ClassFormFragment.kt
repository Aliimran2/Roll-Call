package com.miassolutions.rollcall.ui.screens.classform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.miassolutions.rollcall.databinding.FragmentClassFormBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.setTextIfChanged
import com.miassolutions.rollcall.extenstions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ClassFormFragment : BottomSheetDialogFragment() {

    private var _biding: FragmentClassFormBinding? = null
    private val binding get() = _biding!!

    private val viewModel by viewModels<ClassFormViewModel>()
    private val args by navArgs<ClassFormFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _biding = FragmentClassFormBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.classId?.let {
            viewModel.loadClassForEdit(it)
        }




        setupEventObserver()
        setupListeners()
        setupStateObserver()
    }


    private fun setupEventObserver() {

        collectLatestFlow {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    ClassFormUiEvent.NavigateBack -> {
                        findNavController().navigateUp()
                    }

                    is ClassFormUiEvent.ShowToast -> {
                        showToast(event.message)
                    }
                }
            }
        }

    }

    private fun setupStateObserver() {
        collectLatestFlow {
            viewModel.uiState.collectLatest { state ->
                binding.apply {
                    classNameInput.setTextIfChanged(state.className)
                    classSecInput.setTextIfChanged(state.sectionName)
                    startDateInput.setTextIfChanged(state.startDateStr)
                    endDateInput.setTextIfChanged(state.endDateStr)
                    teacherNameInput.setTextIfChanged(state.teacherName)
                    saveClassButton.text = if (state.isEditMode) "Update" else "Save"
                }

            }
        }
    }

    private fun setupListeners() {

        with(binding) {
            classNameInput.doAfterTextChanged {
                viewModel.onClassNameChange(it.toString())
            }
            classSecInput.doAfterTextChanged {
                viewModel.onSectionNameChange(it.toString())
            }
            teacherNameInput.doAfterTextChanged {
                viewModel.onTeacherNameChange(it.toString())
            }

            // Open date picker on click, not on text change
            startDateInput.setOnClickListener {
                showDatePicker { date ->
                    viewModel.onStartDateChange(date)
                }
            }
            endDateInput.setOnClickListener {
                showDatePicker { date ->
                    viewModel.onEndDateChange(date)
                }
            }



            saveClassButton.setOnClickListener {
                if (validateInputs()) {
                    viewModel.onSavClicked()
                }
            }

        }


    }

    private fun showDatePicker(onDateSelected: (Long) -> Unit) {
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .build()

        picker.show(parentFragmentManager, "DatePicker")

        picker.addOnPositiveButtonClickListener { millis ->
            onDateSelected(millis)
        }
    }


    private fun validateInputs(): Boolean {
        with(binding) {
            classNameLayout.error = null
            classSecLayout.error = null
            teacherNameLayout.error = null
            startDateLayout.error = null
            endDateLayout.error = null

            return when {
                classNameInput.text.isNullOrBlank() -> {
                    classNameLayout.error = "Enter class name"
                    classNameInput.requestFocus()
                    false
                }

                classSecInput.text.isNullOrBlank() -> {
                    classSecLayout.error = "Enter section name"
                    classSecInput.requestFocus()
                    false
                }


                startDateInput.text.isNullOrBlank() -> {
                    startDateLayout.error = "Pick start date"
                    startDateInput.requestFocus()
                    false
                }

                endDateInput.text.isNullOrBlank() -> {
                    endDateLayout.error = "Pick end date"
                    endDateInput.requestFocus()
                    false
                }

                teacherNameInput.text.isNullOrBlank() -> {
                    teacherNameLayout.error = "Enter teacher name"
                    teacherNameInput.requestFocus()
                    false
                }

                else -> true
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _biding = null
    }
}
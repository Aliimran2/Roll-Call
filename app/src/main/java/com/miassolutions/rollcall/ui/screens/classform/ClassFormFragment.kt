package com.miassolutions.rollcall.ui.screens.classform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.databinding.FragmentClassFormBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showToast
import com.miassolutions.rollcall.ui.screens.classscreen.ClassUiEvent
import com.miassolutions.rollcall.ui.viewmodels.ClassViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Date

@AndroidEntryPoint
class ClassFormFragment : BottomSheetDialogFragment() {

    private var _biding: FragmentClassFormBinding? = null
    private val binding get() = _biding!!

    private val viewModel by viewModels<ClassViewModel>()
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

        if (args.classId == null){
            //add class
        } else {
            //update existing class
        }


        setupListeners()
        setupObservers()
    }

    private fun setupObservers() {
        collectLatestFlow {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is ClassUiEvent.ShowToast -> {
                        showToast(event.message)
                    }

                    is ClassUiEvent.NavigateToBack -> findNavController().popBackStack()
                    is ClassUiEvent.NavigateToEditClass -> Unit
                }

            }
        }
    }

    private fun setupListeners() {
        binding.saveClassButton.setOnClickListener {
            if (validateInputs()) {
                val classEntity = createClassEntity()
                viewModel.insertClass(classEntity)
                clearFields()
            }
        }
    }


    private fun validateInputs(): Boolean {
        var isValid = true
        binding.apply {
            classNameLayout.error = null
            teacherNameLayout.error = null
            classSecLayout.error = null

            if (classNameInput.text.isNullOrBlank()) {
                classNameInput.error = "Enter class name"
                classNameInput.requestFocus()
                isValid = false

            }


            if (classSecInput.text.isNullOrBlank()) {
                classSecInput.error = "Enter class name"
                classSecInput.requestFocus()
                isValid = false
            }

            if (teacherNameInput.text.isNullOrBlank()) {
                teacherNameLayout.error = "Enter In Charge name"
                teacherNameInput.requestFocus()
                isValid = false
            }
        }
        return isValid
    }

    private fun createClassEntity(): ClassEntity {
        val className = binding.classNameInput.text.toString().trim()
        var secName = binding.classSecInput.text.toString().trim()
        if (secName == "--None--"){
            secName = ""
        } else {
            secName = "($secName)"
        }

        val classWithSec = "$className $secName"

        return ClassEntity(
            className = classWithSec.trim(),
            startDate = Date().time,
            endDate = Date().time,
            teacher = binding.teacherNameInput.text.toString().trim()
        )
    }

    private fun clearFields() {
        binding.apply {
            classNameInput.text?.clear()
            classSecInput.text?.clear()
            teacherNameInput.text?.clear()
            startDateInput.text?.clear()
            endDateInput.text?.clear()
            classNameLayout.error = null
            classSecLayout.error = null
            teacherNameLayout.error = null
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _biding = null
    }
}
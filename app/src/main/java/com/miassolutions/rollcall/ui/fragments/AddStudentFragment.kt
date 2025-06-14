package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.databinding.FragmentAddStudentBinding
import com.miassolutions.rollcall.ui.viewmodels.AddStudentViewModel
import com.miassolutions.rollcall.utils.Constants.DUPLICATE_REG_NUMBER
import com.miassolutions.rollcall.utils.Constants.DUPLICATE_ROLL_NUMBER
import com.miassolutions.rollcall.utils.StudentInsertResult
import com.miassolutions.rollcall.utils.collectLatestFlow
import com.miassolutions.rollcall.utils.showLongToast
import com.miassolutions.rollcall.utils.showToast
import com.miassolutions.rollcall.utils.toFormattedDate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddStudentFragment : Fragment(R.layout.fragment_add_student) {

    private val viewModel by viewModels<AddStudentViewModel>()

    private var _binding: FragmentAddStudentBinding? = null
    private val binding get() = _binding!!

    private var dob : Long = System.currentTimeMillis()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddStudentBinding.bind(view)

        observeViewModel()
        saveButtonClick()
        datePicker()

    }

    private fun saveButtonClick() {
        binding.saveBtn.setOnClickListener {
            setupSaveBtn()
        }
    }

    private fun datePicker(){
        binding.etDob.setOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Input Birth Date")
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .build()

            datePicker.addOnPositiveButtonClickListener {
                binding.etDob.setText(it.toFormattedDate())
                dob = it
            }
            datePicker.show(parentFragmentManager,datePicker.tag)
        }
    }

    private fun observeViewModel() {
        collectLatestFlow {
            viewModel.toastMessage.collect { result: StudentInsertResult ->
                when (result) {
                    is StudentInsertResult.Failure -> {
                        when (result.reason) {
                            DUPLICATE_REG_NUMBER -> {
                                binding.apply {
                                    etRegNumber.requestFocus()
                                    etRegNumber.error = "Reg no already exists"
                                }
                            }

                            DUPLICATE_ROLL_NUMBER -> {
                                binding.apply {
                                    etRollNumber.requestFocus()
                                    binding.etRollNumber.error = "Roll no already exists"
                                }
                            }

                            else -> {
                                showLongToast("Failed : ${result.reason}")
                            }

                        }

                    }

                    is StudentInsertResult.Success -> {
                        showToast("Student added!")
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }


    private fun setupSaveBtn() {
        binding.apply {

            val regNumber = etRegNumber.text.toString()
            val rollNumber = etRollNumber.text.toString()
            val studentName = etName.text.toString()
            val fatherName = etFatherName.text.toString()
            val phoneNumber = etPhoneNumber.text.toString()

            val address = etAddress.text.toString()

            when {
                regNumber.isBlank() -> {
                    binding.apply {
                        etRegNumber.requestFocus()
                        etRegNumber.error = "Enter reg. number"
                    }
                }

                rollNumber.isBlank() -> {
                    binding.apply {
                        etRollNumber.requestFocus()
                        etRollNumber.error = "Enter roll number"
                    }
                }

                studentName.isBlank() -> {
                    binding.apply {
                        etName.requestFocus()
                        etName.error = "Enter name of the student"
                    }
                }

                fatherName.isBlank() -> {
                    binding.apply {
                        etFatherName.requestFocus()
                        etFatherName.error = "Enter name of the student"
                    }
                }





                else -> {
                    val roll = rollNumber.toInt()
                    val reg = regNumber.toInt()
                    val studentEntity =
                        StudentEntity(
                            regNumber = reg,
                            rollNumber = roll,
                            studentName = studentName,
                            fatherName = fatherName,
                            dob =  dob,
                            phoneNumber = phoneNumber,
                            klass = "8th B",
                            address = address
                        )
                    viewModel.insertStudent(studentEntity)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
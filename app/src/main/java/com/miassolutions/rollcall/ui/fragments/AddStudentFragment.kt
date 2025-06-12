package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddStudentFragment : Fragment(R.layout.fragment_add_student) {

    private val viewModel by viewModels<AddStudentViewModel>()

    private var _binding: FragmentAddStudentBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddStudentBinding.bind(view)

        observeViewModel()
        saveButtonClick()

    }

    private fun saveButtonClick() {
        binding.saveBtn.setOnClickListener {
            setupSaveBtn()
        }
    }

    private fun observeViewModel() {
        collectLatestFlow {
            viewModel.toastMessage.collect { result: StudentInsertResult ->
                when (result) {
                    is StudentInsertResult.Failure -> {
                        when (result.reason) {
                            DUPLICATE_REG_NUMBER -> {
                                binding.etRegNumber.requestFocus()
                                binding.etRegNumber.error = "Reg no already exists"
                            }

                            DUPLICATE_ROLL_NUMBER -> {
                                binding.etRollNumber.requestFocus()
                                binding.etRollNumber.error = "Roll no already exists"
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

            when {
                regNumber.isBlank() -> {
                    binding.etRegNumber.requestFocus()
                    binding.etRegNumber.error = "Enter reg. number"
                }

                rollNumber.isBlank() -> {
                    binding.etRollNumber.requestFocus()
                    binding.etRollNumber.error = "Enter roll number"
                }

                studentName.isBlank() -> {
                    binding.etName.requestFocus()
                    binding.etName.error = "Enter name of the student"
                }

                fatherName.isBlank() -> {
                    binding.etFatherName.requestFocus()
                    binding.etFatherName.error = "Enter name of the student"
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
                            phoneNumber = phoneNumber,
                            klass = "8th B"
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
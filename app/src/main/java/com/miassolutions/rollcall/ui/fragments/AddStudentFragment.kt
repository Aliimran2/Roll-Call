package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.databinding.FragmentAddStudentBinding
import com.miassolutions.rollcall.ui.viewmodels.AddStudentViewModel
import com.miassolutions.rollcall.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddStudentFragment : Fragment(R.layout.fragment_add_student) {

    private val viewModel by viewModels<AddStudentViewModel>()

    private var _binding: FragmentAddStudentBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddStudentBinding.bind(view)




        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toastMessage.collect {
                    showToast(it)
                }

            }

        }


        binding.saveBtn.setOnClickListener {

            setupSaveBtn()
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
                    val student =
                        Student(
                            regNumber = reg,
                            rollNumber = roll,
                            studentName = studentName,
                            fatherName = fatherName,
                            phoneNumber = phoneNumber,
                            klass = "8th B"
                        )
                    viewModel.insertStudent(student)


                    findNavController().navigateUp()
                    showToast("$studentName is added in db")
                }
            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
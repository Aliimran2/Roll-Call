package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.data.repository.StudentFetchResult
import com.miassolutions.rollcall.databinding.FragmentStudentDetailBinding
import com.miassolutions.rollcall.ui.viewmodels.StudentDetailViewModel
import com.miassolutions.rollcall.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentDetailFragment : Fragment(R.layout.fragment_student_detail) {

    private var _binding: FragmentStudentDetailBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<StudentDetailFragmentArgs>()
    private val viewModel by viewModels<StudentDetailViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentDetailBinding.bind(view)

        val studentId = args.id

        loadStudentData()
//        actionButtonsListener(args.id, args.studentName)
        viewModel.fetchStudentById(studentId)

    }

    private fun actionButtonsListener(studentId: String, studentName: String) {
        binding.apply {
            deleteBtn.setOnClickListener {
                viewModel.fetchStudentById(studentId)
                showToast("Deleted $studentName")
                findNavController().navigateUp()
            }

            editNavBtn.setOnClickListener {
                val action =
                    StudentDetailFragmentDirections.actionStudentDetailFragmentToEditStudentFragment(
                        studentId,
                        studentName
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun loadStudentData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.studentState.collect { result ->
                    when (result) {
                        is StudentFetchResult.Error -> {

                        }

                        StudentFetchResult.Loading -> {

                        }

                        is StudentFetchResult.Success<Student> -> {
                            val student = result.data

                            binding.apply {
                                tvRegNo.text = student.regNumber.toString()
                                tvRollNo.text = student.rollNumber.toString()
                                tvStudentName.text = student.studentName
                                tvFatherName.text = student.fatherName
                                tvClass.text = student.klass
                                tvDob.text = student.dob
                                tvMobile.text = student.phoneNumber
                            }

                        }
                    }

                }


            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.data.repository.StudentFetchResult
import com.miassolutions.rollcall.databinding.FragmentStudentProfileBinding
import com.miassolutions.rollcall.databinding.StudentDetailLayoutBinding
import com.miassolutions.rollcall.ui.viewmodels.StudentDetailViewModel
import com.miassolutions.rollcall.utils.collectLatestFlow
import com.miassolutions.rollcall.utils.showLongToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentDetailFragment : Fragment(R.layout.fragment_student_profile) {

    private var _binding: FragmentStudentProfileBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<StudentDetailFragmentArgs>()
    private val viewModel by viewModels<StudentDetailViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentProfileBinding.bind(view)

        val studentId = args.id

        loadStudentData()
//        actionButtonsListener(args.id, args.studentName)
        viewModel.fetchStudentById(studentId)

    }


    private fun loadStudentData() {

        collectLatestFlow {
            viewModel.studentEntityState.collect { result ->
                when (result) {
                    is StudentFetchResult.Error -> {
                        showLongToast(result.message)
                    }

                    StudentFetchResult.Loading -> {/*nothing to do*/
                    }

                    is StudentFetchResult.Success<StudentEntity> -> {
                        val student = result.data

                        binding.apply {
                            primaryProfile.apply {
                                tvStudentName.text = student.studentName
                                tvRegNum.text = "${student.regNumber}"
                                tvRollNum.text = "${student.rollNumber}"
                                tvDob.text = "${student.dob}"
                                tvDoa.text = "${student.dob}" //todo
                                tvBForm.text = "00000-0000000-0"
                            }

                            secondaryProfile.apply {
                                tvFatherName.text = "${student.fatherName}"
                                tvPhoneNumber.text = "${student.phoneNumber}"
                                tvAddress.text = "${student.address}"
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
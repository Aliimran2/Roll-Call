package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil3.load
import coil3.request.placeholder
import com.bumptech.glide.Glide
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.common.Constants.TAG
import com.miassolutions.rollcall.data.entities.StudentEntity

import com.miassolutions.rollcall.databinding.FragmentStudentProfileBinding
import com.miassolutions.rollcall.ui.viewmodels.StudentDetailViewModel
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showLongToast
import com.miassolutions.rollcall.extenstions.toFormattedDate
import com.miassolutions.rollcall.utils.StudentResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
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
        viewModel.setStudentId(studentId)

    }


    private fun loadStudentData() {

//        collectLatestFlow {
//            launch {
//                viewModel.attendanceOfStudent.collectLatest {
//
//                    Log.d("MiasSolutionsDetails", it.toString())
//
//                }
//            }
//
//            launch {
//                viewModel.presentCount.collectLatest {
//                    binding.attendanceGraph.tvPresent.text = "Presence : $it"
//                }
//            }
//
//            launch {
//                viewModel.absentCount.collectLatest {
//                    binding.attendanceGraph.tvAbsent.text = "Absence : $it"
//                }
//            }
//
//            launch {
//                viewModel.attendancePercentage.collectLatest {
//                    binding.attendanceGraph.progressCircular.setProgressCompat(it, true)
//                    binding.attendanceGraph.tvPercentageText.text = "Percentage : $it%"
//                }
//            }
//
//
//            launch {
//                viewModel.studentEntityState.collect { result ->
//                    when (result) {
//                        is StudentResult.Error -> {
//                            showLongToast(result.message)
//                        }
//
//                        StudentResult.Loading -> {/*nothing to do*/
//                        }
//
//                        is StudentResult.Success<StudentEntity> -> {
//                            val student = result.data
//
//                            binding.apply {
//                                primaryProfile.apply {
//                                    student.studentImage?.let {
//                                        Log.d(TAG, it)
//                                        Glide.with(requireContext())
//                                            .load(it)
//                                            .placeholder(R.drawable.ic_person)
//                                            .error(R.drawable.ic_error_image)
//                                            .into(ivProfile)
//
//
//                                    }
//                                    tvStudentName.text = student.studentName
//                                    tvRegNum.text = "${student.regNumber}"
//                                    tvRollNum.text = "${student.rollNumber}"
//                                    tvDob.text = "${student.dob.toFormattedDate()}"
//                                    tvDoa.text = "${student.dob.toFormattedDate()}" //todo
//                                    tvBForm.text = "${student.bForm}"
//                                }
//
//                                secondaryProfile.apply {
//                                    tvFatherName.text = "${student.fatherName}"
//                                    tvPhoneNumber.text = "${student.phoneNumber}"
//                                    tvAddress.text = "${student.address}"
//                                }
//
//
//                            }
//
//                        }
//                    }
//
//                }
//
//            }
//
//
//        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
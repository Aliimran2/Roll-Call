package com.miassolutions.rollcall.ui.screens.studentdetailscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil3.load
import com.google.android.material.appbar.MaterialToolbar
import com.miassolutions.rollcall.R

import com.miassolutions.rollcall.databinding.FragmentStudentProfileBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showSnackbar
import com.miassolutions.rollcall.ui.MainActivity
import com.miassolutions.rollcall.ui.viewmodels.StudentDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentDetailFragment : Fragment(R.layout.fragment_student_profile) {

    private var _binding: FragmentStudentProfileBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<StudentDetailFragmentArgs>()
    private val viewModel by viewModels<StudentDetailViewModel>()

    private lateinit var toolbar: MaterialToolbar


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentProfileBinding.bind(view)

        val studentId = args.studentId

        toolbar = (activity as MainActivity).findViewById(R.id.toolbar)
        toolbar.subtitle = null

        viewModel.fetchStudentById(studentId)
        collectUiState()


    }

    private fun collectUiState() {
        collectLatestFlow {
            launch {
                viewModel.uiState.collectLatest { state ->
                    state.primaryProfile?.let {
                        binding.primaryProfile.apply {
                            ivProfile.load(it.imageUri)
                            tvStudentName.text = it.name
                            tvRollNum.text = it.rollNum
                            tvRegNum.text = it.regNum
                            tvDob.text = it.dateOfBirth
                            tvDoa.text = it.dateOfAdmission
                            tvBForm.text = it.bForm
                        }
                    }
                    state.secondaryProfile?.let {
                        binding.secondaryProfile.apply {
                            tvFatherName.text = it.fatherName
                            tvPhoneNumber.text = it.phoneNumber
                            tvAddress.text = it.address
                        }
                    }


                }
            }

            launch {
                viewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        StudentDetailUiEvent.NavigateBack -> {
                            findNavController().navigateUp()
                        }

                        is StudentDetailUiEvent.ShowSnackbar -> {
                            showSnackbar(event.message)
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
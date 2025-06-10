package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.data.repository.StudentFetchResult
import com.miassolutions.rollcall.databinding.FragmentStudentDetailBinding
import com.miassolutions.rollcall.databinding.StudentDetailLayoutBinding
import com.miassolutions.rollcall.ui.viewmodels.StudentDetailViewModel
import com.miassolutions.rollcall.utils.collectLatestFlow
import com.miassolutions.rollcall.utils.showLongToast
import com.miassolutions.rollcall.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentDetailFragment : Fragment(R.layout.student_detail_layout) {

    private var _binding: StudentDetailLayoutBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<StudentDetailFragmentArgs>()
    private val viewModel by viewModels<StudentDetailViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = StudentDetailLayoutBinding.bind(view)

        val studentId = args.id

        loadStudentData()
        actionButtonsListener(args.id, args.studentName)
        viewModel.fetchStudentById(studentId)

    }

    private fun actionButtonsListener(studentId: String, studentName: String) {
        binding.apply {
            deleteBtn.setOnClickListener {

                MaterialAlertDialogBuilder(requireContext())

                .setTitle("Delete $studentName!!")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes, Delete") { dialog, _ ->
                    viewModel.deleteStudentById(studentId)
                    Snackbar.make(binding.root, "$studentName Deleted", Snackbar.LENGTH_LONG)
                        .show()
                findNavController().navigateUp()
                }
                .setNegativeButton("Cancel", null)
                .show()
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
                            showLongToast(result.message)
                        }

                        StudentFetchResult.Loading -> {/*nothing to do*/
                        }

                        is StudentFetchResult.Success<Student> -> {
                            val student = result.data

                            binding.apply {

                                itemRegNumber.apply {
                                    tvFieldLabel.text = "Reg No"
                                    tvFieldValue.text = student.regNumber.toString()
                                }

                                itemRollNumber.apply {
                                    tvFieldLabel.text = "Roll No"
                                    tvFieldValue.text = student.rollNumber.toString()
                                }


                                itemStudentName.apply {
                                    tvFieldLabel.text = "Student Name"
                                    tvFieldValue.text = student.studentName
                                }
                                itemFatherName.apply {
                                    tvFieldLabel.text = "Father Name"
                                    tvFieldValue.text = student.fatherName
                                }


                                itemClass.apply {
                                    tvFieldLabel.text = "Class"
                                    tvFieldValue.text = student.klass
                                }

                                itemPhoneNumber.apply {
                                    tvFieldLabel.text = "Phone No"
                                    tvFieldValue.text = student.phoneNumber
                                }

                                itemDob.apply {
                                    tvFieldLabel.text = "Date of Birth"
                                    tvFieldValue.text = student.dob
                                }

                                itemAddress.apply {
                                    tvFieldLabel.text = "Address"
                                    tvFieldValue.text = student.address
                                }

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
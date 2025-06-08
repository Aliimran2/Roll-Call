package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentAddStudentBinding
import com.miassolutions.rollcall.databinding.FragmentEditStudentBinding
import com.miassolutions.rollcall.databinding.FragmentStudentDetailBinding
import com.miassolutions.rollcall.utils.StudentProvider
import com.miassolutions.rollcall.utils.showToast

class StudentDetailFragment : Fragment(R.layout.fragment_student_detail) {

    private var _binding: FragmentStudentDetailBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<StudentDetailFragmentArgs>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentDetailBinding.bind(view)

        val studentId = args.id

        loadStudentData(studentId)
        actionButtonsListener(args.id, args.studentName)



    }

    private fun actionButtonsListener(studentId: String, studentName: String){
        binding.apply {
            deleteBtn.setOnClickListener{
                StudentProvider.deleteStudent(studentId)
                showToast("Deleted")
                findNavController().navigateUp()
            }

            editNavBtn.setOnClickListener {
                val action = StudentDetailFragmentDirections.actionStudentDetailFragmentToEditStudentFragment(studentId, studentName)
                findNavController().navigate(action)
            }
        }
    }

    private fun loadStudentData(studentId: String) {
        val student = StudentProvider.findStudent(studentId)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
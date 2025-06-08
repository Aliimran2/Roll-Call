package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentAddStudentBinding
import com.miassolutions.rollcall.databinding.FragmentEditStudentBinding
import com.miassolutions.rollcall.utils.StudentProvider
import com.miassolutions.rollcall.utils.showToast

class EditStudentFragment : Fragment(R.layout.fragment_edit_student) {

    private var _binding : FragmentEditStudentBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditStudentBinding.bind(view)


        binding.saveBtn.setOnClickListener{

        setupSaveBtn()
        }

    }
    private fun setupSaveBtn(){
        val rollNumber = binding.etRollNumber.text.toString()
        val studentName = binding.etName.text.toString()

       when{
           rollNumber.isBlank() -> {
               binding.etRollNumber.requestFocus()
               binding.etRollNumber.error = "Enter roll number"
           }
           studentName.isBlank() -> {
               binding.etName.requestFocus()
               binding.etName.error = "Enter name of the student"
           }

           else -> {
               val roll = rollNumber.toInt()
               StudentProvider.addStudent(roll, studentName)
               findNavController().navigateUp()
               showToast("$studentName is added in db")
           }
       }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
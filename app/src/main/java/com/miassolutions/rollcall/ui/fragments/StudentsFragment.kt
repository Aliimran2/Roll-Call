package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.databinding.FragmentAddStudentBinding
import com.miassolutions.rollcall.databinding.FragmentStudentsBinding
import com.miassolutions.rollcall.ui.adapters.StudentListAdapter
import com.miassolutions.rollcall.utils.StudentProvider

class StudentsFragment : Fragment(R.layout.fragment_students) {





    private lateinit var adapter: StudentListAdapter

    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentsBinding.bind(view)


        setupFabClickListener()
        setupRecyclerView()


    }

    private fun setupFabClickListener() {
        binding.fabAddStudent.setOnClickListener {
            findNavController().navigate(R.id.addStudentFragment)
        }
    }


    private fun setupRecyclerView() {

        adapter = StudentListAdapter { studentId ->
            Toast.makeText(requireContext(), "$studentId is clicked", Toast.LENGTH_SHORT).show()
        }


        adapter.submitList(StudentProvider.students.toList())
        binding.rvStudents.adapter = adapter

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
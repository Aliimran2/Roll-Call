package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.databinding.FragmentAddStudentBinding
import com.miassolutions.rollcall.databinding.FragmentStudentsBinding
import com.miassolutions.rollcall.ui.adapters.StudentListAdapter
import com.miassolutions.rollcall.utils.StudentProvider
import androidx.core.view.isVisible
import com.miassolutions.rollcall.utils.showToast

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

        adapter = StudentListAdapter { student ->
            val action = StudentsFragmentDirections.actionStudentsFragmentToStudentDetailFragment(student.id, student.studentName)
            findNavController().navigate(action)

        }


        adapter.submitList(StudentProvider.students.toList())
        binding.rvStudents.adapter = adapter

        binding.rvStudents.addOnScrollListener(object : OnScrollListener(){
            val fab = binding.fabAddStudent

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy>0 && fab.isVisible){
                Log.d("MiasSolutions", "onScrolled: dy : $dy")
                    fab.hide()
                } else if(dy<0 && fab.visibility != View.VISIBLE) {
                Log.d("MiasSolutions", "onScrolled: dy : $dy")
                    fab.show()
                }
            }
        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
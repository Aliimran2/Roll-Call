package com.miassolutions.rollcall.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentAddStudentBinding
import com.miassolutions.rollcall.databinding.FragmentStudentsBinding

class StudentsFragment : Fragment(R.layout.fragment_students) {

    private var _binding : FragmentStudentsBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentsBinding.bind(view)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
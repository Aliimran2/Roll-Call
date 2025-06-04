package com.miassolutions.rollcall.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentAddStudentBinding

class AddStudentFragment : Fragment(R.layout.fragment_add_student) {

    private var _binding : FragmentAddStudentBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddStudentBinding.bind(view)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
package com.miassolutions.rollcall.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentAttendanceBinding


class AttendanceFragment : Fragment(R.layout.fragment_attendance) {

    private var _binding : FragmentAttendanceBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAttendanceBinding.bind(view)



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
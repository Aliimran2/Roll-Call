package com.miassolutions.rollcall.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.StudentWithAttendance
import com.miassolutions.rollcall.databinding.FragmentAttendanceBinding
import com.miassolutions.rollcall.ui.adapters.AttendanceAdapter
import com.miassolutions.rollcall.utils.AttendanceStatus
import com.miassolutions.rollcall.utils.StudentProvider
import com.miassolutions.rollcall.utils.StudentProvider.students
import java.time.LocalDate
import java.util.Date
import kotlin.math.log


class AttendanceFragment : Fragment(R.layout.fragment_attendance) {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AttendanceAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAttendanceBinding.bind(view)

        setupRecyclerView()


        val list = StudentProvider.getStudentListForToday()






        adapter = AttendanceAdapter()
        adapter.submitList(list)
        binding.rvAttendance.adapter = adapter


    }



    private fun setupRecyclerView() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
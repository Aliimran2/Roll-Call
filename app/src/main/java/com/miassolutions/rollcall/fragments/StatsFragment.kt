package com.miassolutions.rollcall.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentAttendanceBinding
import com.miassolutions.rollcall.databinding.FragmentStatsBinding


class StatsFragment : Fragment(R.layout.fragment_stats) {

    private var _binding : FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStatsBinding.bind(view)



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
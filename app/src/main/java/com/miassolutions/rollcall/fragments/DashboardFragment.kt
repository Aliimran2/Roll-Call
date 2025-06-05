package com.miassolutions.rollcall.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.miassolutions.rollcall.MainActivity
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding : FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        val activity = (activity as MainActivity)
        val toolbar = activity.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.subtitle = "Data : 05-06-2025"
        toolbar.isSubtitleCentered = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}
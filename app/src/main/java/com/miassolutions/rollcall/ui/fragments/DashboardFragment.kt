package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil3.load
import coil3.request.error
import coil3.request.placeholder
import com.bumptech.glide.Glide
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentDashboardBinding

import com.miassolutions.rollcall.ui.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val settingsViewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        observeViewModel()

        binding.apply {
            attendanceCard.apply {
                ivCard.setImageResource(R.drawable.ic_attendances)
                tvCard.text = "Attendance"
            }.root.setOnClickListener {

                val action =
                    DashboardFragmentDirections.actionDashboardFragmentToStatsFragment()
                findNavController().navigate(action)

            }

            settingsCard.apply {
                ivCard.setImageResource(R.drawable.ic_settings)
                tvCard.text = "Settings"
            }.root.setOnClickListener {
                val action = DashboardFragmentDirections.actionDashboardFragmentToSettingsFragment()
                findNavController().navigate(action)

            }

            userCard.apply {
                ivCard.setImageResource(R.drawable.ic_person)
                tvCard.text = "Profile"
            }.root.setOnClickListener {
                val action =
                    DashboardFragmentDirections.actionDashboardFragmentToUserProfileFragment()
                findNavController().navigate(action)
            }

            studentsCard.apply {
                ivCard.setImageResource(R.drawable.ic_students_m)
                tvCard.text = "Students"
            }.root.setOnClickListener {
                val action = DashboardFragmentDirections.actionDashboardFragmentToStudentsFragment()
                findNavController().navigate(action)
            }
        }

    }


    private fun observeViewModel() {
        settingsViewModel.userName.observe(viewLifecycleOwner) {
            it?.let {
                binding.topLayout.tvTitle.text = "Welcome! $it"
            }
        }
        settingsViewModel.instituteName.observe(viewLifecycleOwner) {
            it?.let {
                binding.topLayout.tvSubtitle.text = it
            }
        }

        settingsViewModel.userProfileImage.observe(viewLifecycleOwner){
            it?.let {userImage->
                Glide.with(requireContext())
                    .load(userImage)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_error_image)
                    .into(binding.topLayout.ivUserProfile)
            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
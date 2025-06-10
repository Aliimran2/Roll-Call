package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentSettingsBinding
import com.miassolutions.rollcall.ui.viewmodels.SettingsViewModel
import com.miassolutions.rollcall.utils.collectLatestFlow
import com.miassolutions.rollcall.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        setupButtonClickListener()
        collectLatestFlow {
            viewModel.deleteAllMessage.collect {
                showToast(it)
            }
        }

    }

    private fun setupButtonClickListener() {
        binding.btnDeleteAllStudents.setOnClickListener {

            viewModel.deleteAll()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
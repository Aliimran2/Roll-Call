package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.miassolutions.rollcall.databinding.FragmentUserProfileBinding
import com.miassolutions.rollcall.ui.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.userName.observe(viewLifecycleOwner) { userName ->
            val name = userName ?: "Set name"
            binding.etUserName.setText(name)
        }

        viewModel.instituteName.observe(viewLifecycleOwner) { instName ->
            instName?.let {
                binding.etInstitute.setText(it)
            }
        }




        binding.btnSaveProfile.setOnClickListener {
            val userName = binding.etUserName.text.toString().trim()
            val instituteName = binding.etInstitute.text.toString().trim()

            when {
                userName.isBlank() -> {
                    binding.etUserName.apply {
                        requestFocus()
                        error = "Enter name"
                    }
                }

                instituteName.isBlank() -> {
                    binding.etInstitute.apply {
                        requestFocus()
                        error = "Enter institute name"
                    }
                }

                else -> {
                    viewModel.saveUserProfile(userName, instituteName)
                    findNavController().navigateUp()
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.miassolutions.rollcall.databinding.FragmentUserProfileBinding
import com.miassolutions.rollcall.ui.viewmodels.SettingsViewModel
import com.miassolutions.rollcall.utils.collectLatestFlow
import com.miassolutions.rollcall.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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

        collectLatestFlow {
            viewModel.userName.observe(viewLifecycleOwner) { userName ->
                val name = userName ?: "Set name"
                binding.etUserName.setText(name)
            }
        }

        binding.btnSaveProfile.setOnClickListener {
            val userName = binding.etUserName.text.toString().trim()
            viewModel.saveUserName(userName)
            findNavController().navigateUp()


        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
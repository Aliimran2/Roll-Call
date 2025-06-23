package com.miassolutions.rollcall.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentUserProfileBinding
import com.miassolutions.rollcall.extenstions.showToast
import com.miassolutions.rollcall.utils.StudentImagePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class UserProfileFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<UserProfileViewModel>()

    private lateinit var userImagePicker: StudentImagePicker


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observeViewModels()

    }

    private fun observeViewModels() {

        viewModel.uiState
            .onEach { handleUiState(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)


        viewModel.uiEvent
            .onEach { handleUiEvent(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleUiState(uiState: UserProfileUiState) {
        binding.apply {
            etUserName.setText(uiState.userName.uppercase())
            etInstitute.setText(uiState.instituteName.uppercase())
        }

        uiState.userProfileImage?.let {uri ->
            if (uri.isNotEmpty()){
                loadImage(uri)
            } else {
                binding.ivUserProfile.setImageResource(R.drawable.ic_person)
            }

        }

        binding.btnSaveProfile.isEnabled = !uiState.isLoading
    }

    private fun handleUiEvent(uiEvent: UserProfileUiEvent) {

        when (uiEvent) {
            UserProfileUiEvent.NavigateUp -> {
                findNavController().navigateUp()
            }

            is UserProfileUiEvent.ShowToast -> {
                showToast(uiEvent.message)
            }

            is UserProfileUiEvent.ShowValidationError -> showValidatorError(uiEvent)
        }

    }

    private fun showValidatorError(event: UserProfileUiEvent.ShowValidationError) {
        val targetView = when (event.field) {
            Field.USER_NAME -> binding.etUserName
            Field.INSTITUTE_NAME -> binding.etInstitute
        }

        targetView.apply {
            requestFocus()
            error = event.message
        }
    }


    private fun initViews() {
        userImagePicker = StudentImagePicker(this) { uri ->
            loadImage(uri.toString())
            viewModel.saveImageUrlStr(uri.toString())
        }

        binding.apply {
            ivUserProfile.setOnClickListener { userImagePicker.requestAndPickImage() }
            btnSaveProfile.setOnClickListener { saveProfile() }
        }
    }

    private fun saveProfile() {
        val userName = binding.etUserName.text.toString().trim()
        val instituteName = binding.etInstitute.text.toString().trim()

        viewModel.validateAndSaveProfile(userName, instituteName)

    }

    private fun loadImage(uri: String) {

        Glide.with(requireContext())
            .load(uri)
            .placeholder(R.drawable.ic_person)
            .error(R.drawable.ic_error_image)
            .into(binding.ivUserProfile)




    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
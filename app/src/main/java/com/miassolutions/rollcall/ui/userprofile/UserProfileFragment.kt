package com.miassolutions.rollcall.ui.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentUserProfileBinding
import com.miassolutions.rollcall.utils.StudentImagePicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<UserProfileViewModel>()

    private lateinit var userImagePicker: StudentImagePicker

    private var userImageUriStr = ""

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

        userImagePicker = StudentImagePicker(this) {
            Glide.with(requireContext())
                .load(it)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_error_image)
                .into(binding.ivUserProfile)

            userImageUriStr = it.toString()
            viewModel.saveImageUriStr(userImageUriStr)
        }

        binding.ivUserProfile.setOnClickListener {

            userImagePicker.requestAndPickImage()

        }


//        viewModel.userName.observe(viewLifecycleOwner) { userName ->
//            userName?.let {
//
//                binding.etUserName.setText(it.uppercase())
//            }
//        }
//
//        viewModel.instituteName.observe(viewLifecycleOwner) { instName ->
//            instName?.let {
//                binding.etInstitute.setText(it.uppercase())
//            }
//        }
//
//        viewModel.userProfileImage.observe(viewLifecycleOwner) { imagePath ->
//            imagePath?.let {
//                Glide.with(requireContext())
//                    .load(it)
//                    .placeholder(R.drawable.ic_person)
//                    .error(R.drawable.ic_error_image)
//                    .into(binding.ivUserProfile)
//            }
//
//        }




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
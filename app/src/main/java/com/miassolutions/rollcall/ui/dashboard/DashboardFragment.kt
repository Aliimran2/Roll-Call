package com.miassolutions.rollcall.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentDashboardBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<DashboardViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)

        setupStaticCardContent()
        setupClickListeners()
        observeViewModel()

    }

    private fun observeViewModel() = collectLatestFlow {

        launch {
            viewModel.uiState.collectLatest { uiState ->
                renderUiState(uiState)
            }
        }

        launch {
            viewModel.uiEvent.collectLatest { uiEvent ->
                handleEvent(uiEvent)
            }
        }
    }

    private fun loadImage(imagePath : String){
        Glide.with(requireContext())
            .load(imagePath)
            .placeholder(R.drawable.ic_person)
            .error(R.drawable.ic_error_image)
            .into(binding.topLayout.ivUserProfile)
    }

    private fun renderUiState(uiState: DashboardUiState) {

        binding.topLayout.apply {
            uiState.userProfileImageUri?.let {

                loadImage(it)
            }

            tvTitle.text = getString(R.string.welcome, uiState.userName)
            tvSubtitle.text = uiState.instituteName
        }

        uiState.errorMessage?.let {
            showToast("it")
        }
    }

    private fun handleEvent(uiEvent: DashBoardUiEvent) {
        when (uiEvent) {
            DashBoardUiEvent.NavigateToAttendance -> {

                showToast(uiEvent.toString())

            }

            DashBoardUiEvent.NavigateToSettings -> {

                val action = DashboardFragmentDirections.actionDashboardFragmentToSettingsFragment()
                findNavController().navigate(action)
            }

            DashBoardUiEvent.NavigationToClasses -> {
//                val action = DashboardFragmentDirections.actionDashboardFragmentToListClassFragment()
//                findNavController().navigate(action)
            }

            DashBoardUiEvent.NavigationToProfile -> {
                val action =
                    DashboardFragmentDirections.actionDashboardFragmentToUserProfileFragment()
                findNavController().navigate(action)
            }
        }

    }

    private fun setupClickListeners() {
        binding.apply {
            attendanceCard.root.setOnClickListener {
                viewModel.onAttendanceCardClicked()
            }

            studentsCard.root.setOnClickListener {
                viewModel.onClassesCardClicked()
            }

            userCard.root.setOnClickListener {
                viewModel.onProfileCardClicked()
            }

            settingsCard.root.setOnClickListener {
                viewModel.onSettingsCardClicked()
            }
        }
    }

    private fun setupStaticCardContent() {
        binding.apply {
            attendanceCard.apply {
                ivCard.setImageResource(R.drawable.ic_attendances)
                tvCard.text = getString(R.string.attendance)
            }

            studentsCard.apply {
                ivCard.setImageResource(R.drawable.ic_class)
                tvCard.text = getString(R.string.classes)
            }

            userCard.apply {
                ivCard.setImageResource(R.drawable.ic_person)
                tvCard.text = getString(R.string.profile)
            }

            settingsCard.apply {
                ivCard.setImageResource(R.drawable.ic_settings)
                tvCard.text = getString(R.string.settings)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
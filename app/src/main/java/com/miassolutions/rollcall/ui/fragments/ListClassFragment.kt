package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.databinding.FragmentListClassBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showToast
import com.miassolutions.rollcall.ui.adapters.ClassListAdapter
import com.miassolutions.rollcall.ui.uicommon.ClassUiEvent
import com.miassolutions.rollcall.ui.uicommon.ClassUiState
import com.miassolutions.rollcall.ui.viewmodels.ClassViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ListClassFragment : Fragment(R.layout.fragment_list_class) {

    private var _binding: FragmentListClassBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ClassViewModel>()
    private val adapter by lazy { ClassListAdapter(onNavigationClick = ::onNavigation) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentListClassBinding.bind(view)

        setupRecyclerView()
        observeUiEvent()
        observeUiState()
        setupListeners()

    }

    private fun setupListeners() {
        binding.addClassFab.setOnClickListener {
            val action = ListClassFragmentDirections.actionListClassFragmentToAddClassFragment()
            findNavController().navigate(action)
        }
    }

    private fun observeUiState() {
        collectLatestFlow {
            viewModel.uiState.collectLatest { state: ClassUiState ->
                when (state) {
                    is ClassUiState.Empty -> {
                        adapter.submitList(emptyList())
                        showToast("No class found")
                    }

                    is ClassUiState.Loading -> {
                        showToast("Loading...")
                    }

                    is ClassUiState.Success -> {
                        adapter.submitList(state.classList)
                    }

                    is ClassUiState.Failure -> {
                        showToast(state.message)
                    }
                }
            }
        }
    }

    private fun observeUiEvent() {
        collectLatestFlow {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    ClassUiEvent.NavigateToBack -> {findNavController().popBackStack()}
                    ClassUiEvent.NavigateToEditClass -> {}
                    is ClassUiEvent.ShowToast -> {
                        showToast(event.message)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvClass.adapter = adapter
    }

    private fun onNavigation(classEntity: ClassEntity) {
        showToast("Navigation ${classEntity.className}")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
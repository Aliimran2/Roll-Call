package com.miassolutions.rollcall.ui.screens.classscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.databinding.FragmentClassListBinding
import com.miassolutions.rollcall.extenstions.addMenu
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showConfirmationDialog
import com.miassolutions.rollcall.extenstions.showPopupMenu
import com.miassolutions.rollcall.extenstions.showToast
import com.miassolutions.rollcall.ui.MainActivity
import com.miassolutions.rollcall.ui.adapters.ClassListAdapter
import com.miassolutions.rollcall.ui.viewmodels.ClassViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ClassListFragment : Fragment(R.layout.fragment_class_list) {

    private var _binding: FragmentClassListBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ClassViewModel>()
    private val adapter by lazy {
        ClassListAdapter(
            onStudentsClick = ::onStudentsNavigation,
            onAttendanceClick = ::onAttendanceNavigation,
            onReportClick = ::onReportNavigation,
            onMoreClick = ::onMoreClick
        )
    }

    private lateinit var toolbar: MaterialToolbar



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentClassListBinding.bind(view)

        toolbar = (activity as MainActivity).findViewById<MaterialToolbar>(R.id.toolbar)

        setupRecyclerView()
        observeUiEvent()
        observeUiState()
        setupMenu()

    }

    private fun setupMenu() {
        addMenu(R.menu.class_list_menus) { item ->
            when (item.itemId) {
                R.id.action_add_class -> {
                    val action = ClassListFragmentDirections.toAddClassFragment(null)
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }
    }

    private fun onStudentsNavigation(classEntity: ClassEntity) {
        val action = ClassListFragmentDirections.toStudentListFragment(
            classEntity.classId,
            classEntity.className
        )
        findNavController().navigate(action)
    }

    private fun onAttendanceNavigation(classEntity: ClassEntity) {
        val action = ClassListFragmentDirections.toAttendanceListFragment()
        findNavController().navigate(action)
    }

    private fun onReportNavigation(classEntity: ClassEntity) {
        val action = ClassListFragmentDirections.toAttendanceFragment("Report")
        findNavController().navigate(action)
    }

    private fun onMoreClick(view: View, classEntity: ClassEntity) {
        showPopupMenu(view, R.menu.class_poup_menus) { menuItem ->
            when (menuItem.itemId) {

                R.id.action_edit -> {
                    val action = ClassListFragmentDirections.toAddClassFragment(classEntity.classId)
                    findNavController().navigate(action)

                    true
                }

                R.id.action_delete -> {
                    showConfirmationDialog(
                        "Attention!!",
                        "This will delete all data related to the class"
                    ) {
                        viewModel.deleteClass(classEntity)
                    }
                    true
                }

                else -> false
            }
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
//                        showToast("Loading...")
                    }

                    is ClassUiState.Success -> {
                        adapter.submitList(state.classList)
                        toolbar.subtitle = "Total Classes: ${state.totalClasses}"

                    }

                    is ClassUiState.Failure -> {
                        showToast(state.message)
                    }

                    is ClassUiState.ClassDetail -> {

                    }
                }
            }
        }
    }

    private fun observeUiEvent() {
        collectLatestFlow {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    ClassUiEvent.NavigateToBack -> {
                        findNavController().popBackStack()
                    }

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


    override fun onDestroyView() {
        super.onDestroyView()
//        toolbar.subtitle = null
        _binding = null
    }
}
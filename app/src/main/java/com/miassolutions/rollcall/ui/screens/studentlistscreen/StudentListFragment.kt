package com.miassolutions.rollcall.ui.screens.studentlistscreen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.appbar.MaterialToolbar
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentStudentsBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showConfirmationDialog
import com.miassolutions.rollcall.extenstions.showSnackbar
import com.miassolutions.rollcall.extenstions.showToast
import com.miassolutions.rollcall.ui.adapters.StudentListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class StudentListFragment : Fragment(R.layout.fragment_students) {

    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<StudentListViewModel>()
    private val args by navArgs<StudentListFragmentArgs>()


        private lateinit var toolbar: MaterialToolbar
    private lateinit var adapter: StudentListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentsBinding.bind(view)

        viewModel.updateClassId(args.classId, args.className)
        toolbar = (activity as AppCompatActivity).findViewById(R.id.toolbar)


        setupRecyclerView()
        setupFabClickListener()
        observeUiState()
        observeUiEvent()
        setupSearchBar()
    }


    private fun setupSearchBar() {
        binding.searchInput.addTextChangedListener { text ->
            viewModel.onSearchQueryUpdate(text.toString())
        }
    }

    private fun observeUiState() {
        collectLatestFlow {
            viewModel.uiState.collectLatest { state ->
                adapter.submitList(state.studentList)
                toolbar.subtitle = "Total Students: ${state.totalCount}"
            }
        }
    }

    private fun observeUiEvent() {
        collectLatestFlow {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is StudentListUiEvent.DialPhone -> {
                        dialPhoneNumber(event.phone)

                    }

                    is StudentListUiEvent.NavigateToAddOrEdit -> {
                        val action = StudentListFragmentDirections.toAddUpdateStudent(
                            classId = event.classId,
                            className = event.className,
                            studentId = event.studentId
                        )

                        findNavController().navigate(action)
                    }

                    is StudentListUiEvent.NavigateToStudentDetail -> {
                        val action = StudentListFragmentDirections.toStudentDetailFragment(
                            event.studentId,
                            event.studentName
                        )
                        findNavController().navigate(action)

                    }

                    is StudentListUiEvent.ShowDeleteConfirmation -> {
                        showConfirmationDialog(
                            "Attention!!",
                            "This will delete all records related to the student"
                        ) {
                            viewModel.deleteStudent(event.studentId)
                        }

                    }

                    is StudentListUiEvent.ShowSnackbar -> {
                        showSnackbar(event.message)
                    }


                }

            }
        }
    }

    private fun setupFabClickListener() {
        binding.fabAddStudent.setOnClickListener {
            viewModel.onAddStudentClicked()
        }

    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showToast("No dialer app found")
        }
    }

    private fun setupRecyclerView() {
        adapter = StudentListAdapter(

            onPhoneClick = viewModel::onPhoneClicked,
            onProfileClick = viewModel::onStudentClicked,
            onReportClick = viewModel::onReportClicked,
            onEditClick = viewModel::onUpdateStudentClicked,
            onDeleteClick = viewModel::onDeleteClicked
        )

        binding.rvStudents.adapter = adapter

        binding.rvStudents.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (dy > 0) binding.fabAddStudent.hide()
                else if (dy < 0) binding.fabAddStudent.show()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}

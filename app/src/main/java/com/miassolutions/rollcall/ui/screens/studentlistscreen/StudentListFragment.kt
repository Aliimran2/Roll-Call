package com.miassolutions.rollcall.ui.screens.studentlistscreen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.databinding.FragmentStudentsBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showConfirmationDialog
import com.miassolutions.rollcall.extenstions.showSnackbar
import com.miassolutions.rollcall.extenstions.showToast
import com.miassolutions.rollcall.ui.MainActivity
import com.miassolutions.rollcall.ui.adapters.StudentListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class StudentListFragment : Fragment(R.layout.fragment_students) {

    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!

    private val studentListViewModel by viewModels<StudentListViewModel>()
    private val args by navArgs<StudentListFragmentArgs>()


    private lateinit var toolbar: MaterialToolbar
    private lateinit var adapter: StudentListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentsBinding.bind(view)

        studentListViewModel.updateClassId(args.classId)
        toolbar = (activity as MainActivity).findViewById<MaterialToolbar>(R.id.toolbar)


        setupRecyclerView()
        setupFabClickListener()
        observeViewModel()
        setupSearchBar()
    }


    private fun setupSearchBar() {
        binding.searchInput.addTextChangedListener { text ->
            studentListViewModel.onSearchQueryChanged(text.toString())
        }
    }


    private fun observeViewModel() {

        collectLatestFlow {
            studentListViewModel.filteredStudents.collectLatest {
                adapter.submitList(it)
            }
        }
    }


    private fun setupFabClickListener() {
        binding.fabAddStudent.setOnClickListener {
            val action =
                StudentListFragmentDirections.toAddUpdateStudent(args.classId, args.className)
            findNavController().navigate(action)
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

    private fun navToDetail(studentEntity: StudentEntity) {
        val action = StudentListFragmentDirections.toStudentDetailFragment(
            studentEntity.studentId, studentEntity.studentName
        )
        findNavController().navigate(action)
    }

    private fun navToEdit(studentId: String) {
        val action =
            StudentListFragmentDirections.toAddUpdateStudent(
                studentId = studentId,
                classId = args.classId,
                className = args.className
            )
        findNavController().navigate(action)
    }


    private fun setupRecyclerView() {
        adapter = StudentListAdapter(
            onPhoneClick = ::dialPhoneNumber,
            onProfileClick = ::navToDetail,
            onReportClick = ::reportClickListener,
            onEditClick = ::navToEdit,
            onDeleteClick = ::deleteClickListener
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

    private fun deleteClickListener(studentId: String) {

        showConfirmationDialog(
            "Attention!!",
            "This will delete all record related to the student"
        ) {
            studentListViewModel.deleteStudentById(studentId)
            showSnackbar("Student deleted")
        }
    }


    private fun reportClickListener(studentId: String) {
        showToast("Showing report for $studentId")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        toolbar.subtitle = null
        _binding = null
    }
}

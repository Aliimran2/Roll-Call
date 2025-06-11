package com.miassolutions.rollcall.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.snackbar.Snackbar
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.databinding.FragmentStudentsBinding
import com.miassolutions.rollcall.ui.adapters.StudentListAdapter
import com.miassolutions.rollcall.ui.viewmodels.AddStudentViewModel
import com.miassolutions.rollcall.utils.ImportFromExcel
import com.miassolutions.rollcall.utils.collectLatestFlow
import com.miassolutions.rollcall.utils.showSnackbar
import com.miassolutions.rollcall.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class StudentsFragment : Fragment(R.layout.fragment_students) {

    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!

    private val addStudentViewModel by viewModels<AddStudentViewModel>()
    private lateinit var adapter: StudentListAdapter
    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentsBinding.bind(view)

        setupRecyclerView()
        setupFabClickListener()
        setupMenuProvider()
        observeViewModel()
        setupFilePicker()
    }

    private fun setupFilePicker() {
        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
                uri?.let { handleExcelFile(it) }
            }
    }

    private fun pickExcelFile() {
        filePickerLauncher.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
    }

    private fun handleExcelFile(uri: Uri) {
        val dialog = ImportProgressDialogFragment()
        dialog.show(parentFragmentManager, ImportProgressDialogFragment.TAG)

        lifecycleScope.launch {
            try {
                val students = withContext(Dispatchers.IO) {
                    ImportFromExcel.readStudentsFromExcel(requireContext(), uri)
                }

                if (students.isEmpty()) {
                    showSnackbar("No valid students found in file.")
                } else {
                    Log.d("ImportDebug", "Read ${students.size} students from file")
                    addStudentViewModel.importStudents(students)
                }

            } catch (e: Exception) {
                Log.e("ImportError", "Error reading Excel: ${e.message}", e)
                showSnackbar("Failed to import: ${e.localizedMessage ?: "Invalid Excel file."}")
            } finally {
                (parentFragmentManager.findFragmentByTag(ImportProgressDialogFragment.TAG) as? DialogFragment)?.dismiss()
            }
        }
    }

    private fun observeViewModel() {
        collectLatestFlow {
            addStudentViewModel.allStudents.collectLatest {
                adapter.submitList(it)
            }
        }

        collectLatestFlow {
            addStudentViewModel.importUIState.collectLatest { state ->
                binding.progressBar.isVisible = state is AddStudentViewModel.ImportUIState.Importing

                when (state) {
                    is AddStudentViewModel.ImportUIState.Success -> {
                        showSnackbar("Imported: ${state.successCount}, Skipped: ${state.failureCount}")
                    }

                    is AddStudentViewModel.ImportUIState.Error -> {
                        showSnackbar(state.message)
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setupMenuProvider() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                inflater.inflate(R.menu.student_list_fragment, menu)
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_import_excel -> {
                        pickExcelFile()
                        true
                    }

                    R.id.action_export_excel -> {
                        // TODO: Implement export
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupFabClickListener() {
        binding.fabAddStudent.setOnClickListener {
            findNavController().navigate(R.id.addStudentFragment)
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

    private fun navToDetail(student: Student) {
        val action = StudentsFragmentDirections.actionStudentsFragmentToStudentDetailFragment(
            student.studentId, student.studentName
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
        showToast(studentId)
    }

    private fun navToEdit(student: Student) {
        val action = StudentsFragmentDirections.actionStudentsFragmentToEditStudentFragment(
            student.studentId,
            student.studentName
        )

        findNavController().navigate(action)
    }

    private fun reportClickListener(studentId: String) {
        showToast("Showing report for $studentId")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

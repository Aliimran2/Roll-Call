package com.miassolutions.rollcall.ui.screens.studentlistscreen

import ImportFromExcel
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.appbar.MaterialToolbar
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.databinding.FragmentStudentsBinding
import com.miassolutions.rollcall.extenstions.addMenu
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showConfirmationDialog
import com.miassolutions.rollcall.extenstions.showSnackbar
import com.miassolutions.rollcall.extenstions.showToast
import com.miassolutions.rollcall.ui.MainActivity
import com.miassolutions.rollcall.ui.adapters.StudentListAdapter
import com.miassolutions.rollcall.ui.common.ImportProgressDialogFragment
import com.miassolutions.rollcall.ui.viewmodels.AddStudentViewModel
import com.miassolutions.rollcall.ui.viewmodels.StudentDetailViewModel
import com.miassolutions.rollcall.utils.UiState
import com.miassolutions.rollcall.utils.exportExcelToDownloadsWithMediaStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class StudentListFragment : Fragment(R.layout.fragment_students) {

    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!

    private val addStudentViewModel by viewModels<AddStudentViewModel>()
    private val studentListViewModel by viewModels<StudentLitViewModel>()
    private val studentDetailViewModel by viewModels<StudentDetailViewModel>()

    private val args by navArgs<StudentListFragmentArgs>()

    private lateinit var toolbar: MaterialToolbar

    private lateinit var adapter: StudentListAdapter
    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentsBinding.bind(view)

        studentListViewModel.updateClassId(args.classId)
        toolbar = (activity as MainActivity).findViewById<MaterialToolbar>(R.id.toolbar)


        setupRecyclerView()
        setupFabClickListener()
        setupMenuProvider()
        observeViewModel()
        setupFilePicker()
        setupSearchBar()
    }


    private fun setupSearchBar() {
        binding.searchInput.addTextChangedListener { text ->
            studentListViewModel.onSearchQueryChanged(text.toString())
        }
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
            addStudentViewModel.noOfTotalStudents.collectLatest {
                toolbar.subtitle = "Total Students : $it"
            }
        }


        collectLatestFlow {
            studentListViewModel.filteredStudents.collectLatest {
                adapter.submitList(it)
            }
        }

        collectLatestFlow {
            addStudentViewModel.importUIState.collectLatest { state ->
                binding.progressBar.isVisible = state is UiState.Loading

                when (state) {
                    is UiState.Success -> {
                        showSnackbar("Imported: ${state.data.first}, Skipped: ${state.data.second}")
                    }

                    is UiState.Error -> {
                        showSnackbar(state.message)
                    }

                    else -> Unit
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupMenuProvider() {

        addMenu(R.menu.student_list_fragment){menuItem ->
            when(menuItem.itemId){
                R.id.action_import_excel -> {
                    pickExcelFile()
                    true
                }

                R.id.action_export_excel -> {
                    lifecycleScope.launch {
                        val dialog = ImportProgressDialogFragment()
                        dialog.show(parentFragmentManager, ImportProgressDialogFragment.TAG)

                        try {
                            val studentList = addStudentViewModel.filteredStudents.value

                            if (studentList.isNotEmpty()) {
                                withContext(Dispatchers.IO) {
                                    exportExcelToDownloadsWithMediaStore(requireContext(), studentList)
                                }
                                // Make sure showing Snackbar on Main thread
                                withContext(Dispatchers.Main) {
                                    showSnackbar("Excel exported to Downloads")
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    showSnackbar("No students to export")
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            withContext(Dispatchers.Main) {
                                showSnackbar("Export failed: ${e.localizedMessage}")
                            }
                        } finally {
                            dialog.dismiss()
                        }
                    }
                    true
                }
                else -> false
            }
        }

    }

    private fun setupFabClickListener() {
        binding.fabAddStudent.setOnClickListener {
            val action = StudentListFragmentDirections.actionStudentsFragmentToAddStudentFragment(args.classId, args.className)
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
        val action = StudentListFragmentDirections.actionStudentsFragmentToStudentDetailFragment(
            studentEntity.studentId, studentEntity.studentName
        )
        findNavController().navigate(action)
    }

    private fun navToEdit(studentId: String) {
        val action =
            StudentListFragmentDirections.actionStudentsFragmentToAddStudentFragment(
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
            ){
//                studentDetailViewModel.deleteStudentById(studentId)
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

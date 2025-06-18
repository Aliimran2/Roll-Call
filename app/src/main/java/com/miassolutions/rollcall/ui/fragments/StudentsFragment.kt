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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.databinding.FragmentStudentsBinding
import com.miassolutions.rollcall.ui.MainActivity
import com.miassolutions.rollcall.ui.adapters.StudentListAdapter
import com.miassolutions.rollcall.ui.viewmodels.AddStudentViewModel
import com.miassolutions.rollcall.ui.viewmodels.StudentDetailViewModel
import com.miassolutions.rollcall.utils.ImportFromExcel
import com.miassolutions.rollcall.utils.collectLatestFlow
import com.miassolutions.rollcall.utils.exportExcelToDownloadsWithMediaStore
import com.miassolutions.rollcall.utils.showSnackbar
import com.miassolutions.rollcall.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class StudentsFragment : Fragment(R.layout.fragment_students) {

    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!

    private val addStudentViewModel by viewModels<AddStudentViewModel>()
    private val studentDetailViewModel by viewModels<StudentDetailViewModel>()

    private lateinit var toolbar: MaterialToolbar

    private lateinit var adapter: StudentListAdapter
    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentsBinding.bind(view)


        toolbar = (activity as MainActivity).findViewById<MaterialToolbar>(R.id.toolbar)


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
            addStudentViewModel.totalStudents.collectLatest {

                toolbar.subtitle = "Total Students : $it"
            }
        }


        collectLatestFlow {
            addStudentViewModel.filteredStudents.collectLatest {
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

                val searchItem = menu.findItem(R.id.app_bar_search)
                val searchView = searchItem.actionView as? SearchView
                searchView?.queryHint = "Name, Roll, Reg..."



                searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {

                        addStudentViewModel.onSearchQueryChanged(newText.orEmpty())

                        return true
                    }
                })
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
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

    private fun navToDetail(studentEntity: StudentEntity) {
        val action = StudentsFragmentDirections.actionStudentsFragmentToStudentDetailFragment(
            studentEntity.studentId, studentEntity.studentName
        )
        findNavController().navigate(action)
    }

    private fun navToEdit(studentId: String) {
        val action =
            StudentsFragmentDirections.actionStudentsFragmentToAddStudentFragment(studentId)
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
        MaterialAlertDialogBuilder(requireContext())

            .setTitle("Confirm Deletion!!")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes, Delete") { dialog, _ ->
                studentDetailViewModel.deleteStudentById(studentId)
                Snackbar.make(binding.root, "Deleted", Snackbar.LENGTH_LONG)
                    .show()
                dialog.dismiss()

            }
            .setNegativeButton("Cancel", null)
            .show()
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

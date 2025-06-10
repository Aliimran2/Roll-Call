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

    private lateinit var adapter: StudentListAdapter

    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!

    private val addStudentViewModel by viewModels<AddStudentViewModel>()

    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentsBinding.bind(view)


        filePickerLauncher = registerForActivityResult(
            ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            uri?.let {
                handleExcelFile(it)
            }
        }

        observeViewModel()
        setupMenuProvider()
        setupFabClickListener()
        setupRecyclerView()


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
                    addStudentViewModel.importStudents(students)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                showSnackbar("Failed to import: ${e.localizedMessage ?: "Invalid Excel file."}")
            } finally {
                (parentFragmentManager.findFragmentByTag(ImportProgressDialogFragment.TAG) as? DialogFragment)?.dismiss()
            }
        }


    }

    private fun observeViewModel() {
        collectLatestFlow {
            addStudentViewModel.allStudents.collectLatest {
                Log.d("MiasSolution_RoomList", "$it")
                adapter.submitList(it)
            }
        }

        collectLatestFlow {
            addStudentViewModel.importUIState.collectLatest { state ->
                when (state) {
                    is AddStudentViewModel.ImportUIState.Idle -> binding.progressBar.isVisible = false
                    is AddStudentViewModel.ImportUIState.Importing -> binding.progressBar.isVisible =true
                    is AddStudentViewModel.ImportUIState.Success -> {
                        binding.progressBar.isVisible = false
                        showSnackbar("Imported : ${state.successCount}, skipped : ${state.failureCount}")
                    }
                    is AddStudentViewModel.ImportUIState.Error -> {
                        binding.progressBar.isVisible = false
                        showSnackbar("${state.message}")
                    }
                }
            }
        }

    }

    private fun setupMenuProvider() {

        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.student_list_fragment, menu)
            }

            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_import_excel -> {
                        pickExcelFile()
                        true
                    }

                    R.id.action_export_excel -> {
                        //todo()
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
            Toast.makeText(requireContext(), "No dialer app found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navToDetail(student: Student) {
        val action = StudentsFragmentDirections.actionStudentsFragmentToStudentDetailFragment(
            student.studentId,
            student.studentName
        )
        findNavController().navigate(action)
    }


    private fun setupRecyclerView() {

        adapter = StudentListAdapter(
            onPhoneClick = { phoneNumber -> dialPhoneNumber(phoneNumber) },
            onItemClick = { student -> navToDetail(student) }
        )



        binding.rvStudents.adapter = adapter

        binding.rvStudents.addOnScrollListener(object : OnScrollListener() {
            val fab = binding.fabAddStudent

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab.isVisible) {
                    fab.hide()
                } else if (dy < 0 && fab.visibility != View.VISIBLE) {
                    fab.show()
                }
            }
        })

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
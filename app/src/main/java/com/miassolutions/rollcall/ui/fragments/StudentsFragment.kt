package com.miassolutions.rollcall.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.databinding.FragmentStudentsBinding
import com.miassolutions.rollcall.ui.adapters.StudentListAdapter
import com.miassolutions.rollcall.utils.StudentProvider
import com.miassolutions.rollcall.utils.showToast

class StudentsFragment : Fragment(R.layout.fragment_students) {

    private lateinit var adapter: StudentListAdapter

    private var _binding: FragmentStudentsBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStudentsBinding.bind(view)


        setupMenuProvider()
        setupFabClickListener()
        setupRecyclerView()


    }

    private fun setupMenuProvider() {

        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.student_list_fragment, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_import_excel -> {
                        showToast("Importing")
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
            student.id,
            student.studentName
        )
        findNavController().navigate(action)
    }


    private fun setupRecyclerView() {

        adapter = StudentListAdapter(
            onPhoneClick = { phoneNumber -> dialPhoneNumber(phoneNumber) },
            onItemClick = { student -> navToDetail(student) }
        )


        adapter.submitList(StudentProvider.students.toList())
        binding.rvStudents.adapter = adapter

        binding.rvStudents.addOnScrollListener(object : OnScrollListener() {
            val fab = binding.fabAddStudent

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && fab.isVisible) {
                    Log.d("MiasSolutions", "onScrolled: dy : $dy")
                    fab.hide()
                } else if (dy < 0 && fab.visibility != View.VISIBLE) {
                    Log.d("MiasSolutions", "onScrolled: dy : $dy")
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
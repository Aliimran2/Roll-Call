package com.miassolutions.rollcall.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentSettingsBinding
import com.miassolutions.rollcall.ui.viewmodels.SettingsViewModel
import com.miassolutions.rollcall.utils.Constants
import com.miassolutions.rollcall.utils.collectLatestFlow
import com.miassolutions.rollcall.utils.showSnackbar
import com.miassolutions.rollcall.utils.toFormattedDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SettingsViewModel>()
    private var selectedMinDate: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        setupButtonClickListener()
        collectFlow()
        setupDateChangeListener()



        binding.etMinDatePicker.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToDatePickerFragment()
            findNavController().navigate(action)
        }

        binding.btnResetSessionDate.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirm!")
                .setMessage("Are you sure to reset date")
                .setPositiveButton("Yes, Sure") { _, _ ->
                    viewModel.resetDate()
                    showSnackbar("Session date reset")
                }
                .setNegativeButton("Cancel", null)
                .show()

        }

        binding.btnSaveMinDate.setOnClickListener {
            selectedMinDate?.let {
                viewModel.saveMinDate(it)
                showSnackbar("Session date set to ${it.toFormattedDate()}")
            }
        }


    }

    private fun setupDateChangeListener() {
        parentFragmentManager.setFragmentResultListener(
            Constants.DATE_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val selectedDate = bundle.getLong(Constants.SELECTED_DATE)
            binding.etMinDatePicker.setText(selectedDate.toFormattedDate())
            selectedMinDate = selectedDate

        }
    }

    private fun collectFlow() {
        collectLatestFlow {

            launch {
                viewModel.minDate.collectLatest {
                    binding.etMinDatePicker.setText(it?.toFormattedDate())
                }
            }
            launch {
                viewModel.messageEvent.collect {
                    showSnackbar(it)
                }
            }


        }
    }

    private fun setupButtonClickListener() {
        binding.btnDeleteAllStudents.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Caution!!")
                .setMessage("Are you sure? It will delete all students.")
                .setPositiveButton("Yes, Delete") { dialog, _ ->
                    viewModel.deleteAll()
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                .setNegativeButton("Cancel", null)
                .show()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
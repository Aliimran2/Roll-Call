package com.miassolutions.rollcall.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.common.Constants
import com.miassolutions.rollcall.databinding.FragmentSettingsBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showLongToast
import com.miassolutions.rollcall.extenstions.showSnackbar
import com.miassolutions.rollcall.extenstions.toFormattedDate
import com.miassolutions.rollcall.ui.viewmodels.SettingsViewModel
import com.miassolutions.rollcall.utils.copySampleExcelFromAssets
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SettingsViewModel>()





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        setupButtonClickListener()
        collectFlow()
//
//        binding.chipToggle.setOnClickListener {
//            val current = binding.chipToggle.isChecked
//            viewModel.saveSaturdayStatus(current)
//        }

        binding.chipToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                buttonView.text = "Saturday Disabled"
                buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                buttonView.text = "Saturday Enabled"
                buttonView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_two))
            }
            binding.chipToggle.isChecked = isChecked
            viewModel.saveSaturdayStatus(isChecked)
        }

        binding.btnExcelDownload.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                copySampleExcelFromAssets(requireContext(), "sample_students.xlsx")

                showLongToast("Sample Excel exported to Downloads")
            } else {
                showLongToast("Export supported only on Android 10+")
            }

        }

    }


    private fun collectFlow() {
        viewModel.disableSaturday.observe(viewLifecycleOwner){
            binding.chipToggle.isChecked = it
        }

        collectLatestFlow {
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
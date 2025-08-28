package com.miassolutions.rollcall.ui.settings

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil3.Uri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentSettingsBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showLongToast
import com.miassolutions.rollcall.extenstions.showSnackbar
import com.miassolutions.rollcall.extenstions.showToast
import com.miassolutions.rollcall.helper.NotificationHelper
import com.miassolutions.rollcall.utils.copySampleExcelFromAssets
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.jar.Manifest
import androidx.core.net.toUri

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted ->
        if (isGranted){
            showDownloadNotification()
        } else {
            showLongToast("Notification permission denied. Enable it in settings to receive.")
        }
    }

    private fun checkNotificationPermissionAndShow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    showDownloadNotification()
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    showToast("Notification permission is needed to alert you about downloads")
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }

            }


        } else {
            showDownloadNotification()
        }
    }

    private fun showDownloadNotification() {
        val fileName = "SampleStudents.xlsx"
        val fileUri = "".toUri()//todo
        val notificationHelper = NotificationHelper(requireContext())
        notificationHelper.showDownloadCompleteNotification(fileUri, fileName)

    }


    private val viewModel by viewModels<SettingsViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        setupListeners()
        collectFlow()


    }

    private fun setupListeners() {


        binding.btnExcelDownload.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                checkNotificationPermissionAndShow()
                copySampleExcelFromAssets(requireContext(), "sample_students.xlsx")

                showSnackbar("Sample Excel exported to Downloads")
            } else {
                showSnackbar("Export supported only on Android 10+")
            }

        }

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


    private fun collectFlow() {


        collectLatestFlow {
            launch {
                viewModel.messageEvent.collect {
                    showSnackbar(it)
                }
            }


        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
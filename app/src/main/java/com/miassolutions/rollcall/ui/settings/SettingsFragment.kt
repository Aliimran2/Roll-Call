package com.miassolutions.rollcall.ui.settings

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.databinding.FragmentSettingsBinding
import com.miassolutions.rollcall.extenstions.collectLatestFlow
import com.miassolutions.rollcall.extenstions.showSimpleDialog
import com.miassolutions.rollcall.extenstions.showSnackbar
import com.miassolutions.rollcall.notification.NotificationHelper
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var helper: NotificationHelper

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        setupListeners()
        collectFlow()
    }

    private fun checkAndShowNotification(onGranted: () -> Unit) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionX.init(this)
                .permissions(android.Manifest.permission.POST_NOTIFICATIONS)
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(
                        deniedList,
                        "This permission is required to show you important notifications.",
                        "Allow",
                        "Deny"
                    )
                }
                .onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        "You have denied permissions. Please enable it from the settings.",
                        "Go to settings",
                        "Cancel"
                    )
                }
                .request { granted, _, _ ->
                    if (granted) {
                        onGranted()
                    } else {
                        showSnackbar("Permission denied permanently")
                    }
                }
        } else {
            onGranted()
        }

    }



    private fun setupListeners() {

        binding.btnExcelDownload.setOnClickListener {
            checkAndShowNotification {

                helper.notifyFileFromAssets("sample_students.xlsx")
            }
        }


        binding.btnDeleteAllStudents.setOnClickListener {
            showSimpleDialog("Caution!!","Are you sure? It will delete all students record."){
//                viewModel.deleteAll()
                showSnackbar("working")
                findNavController().navigateUp()
            }
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
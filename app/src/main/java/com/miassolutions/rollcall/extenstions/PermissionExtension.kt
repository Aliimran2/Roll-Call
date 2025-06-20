package com.miassolutions.rollcall.extenstions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.apache.logging.log4j.message.Message
import androidx.core.net.toUri

fun Fragment.requestPermissionWithRationale(
    permission: String,
    rationale: String,
    onResult: (Boolean) -> Unit,
){
    val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted ->
        onResult(isGranted)
    }

    when {
        ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
            onResult(true)
        }

        shouldShowRequestPermissionRationale(permission) -> {
            showRationaleDialog(requireContext(), rationale){
                launcher.launch(permission)
            }
        }

        else -> launcher.launch(permission)
    }
}


private fun showRationaleDialog(
    context: Context,
    message: String,
    onAllow: () -> Unit,
){
    AlertDialog.Builder(context)
        .setTitle("Permission Required")
        .setMessage(message)
        .setPositiveButton("Allow"){_, _ -> onAllow()}
        .setNegativeButton("Cancel", null)
        .show()
}


fun Fragment.openSettings(){
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = "package:${requireContext().packageName}".toUri()
    }
    startActivity(intent)
}
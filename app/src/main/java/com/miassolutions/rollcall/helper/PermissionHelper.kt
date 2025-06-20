package com.miassolutions.rollcall.helper

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment

class PermissionHelper(private val fragment: Fragment) {

    interface PermissionCallback {
        fun onPermissionResult(permission: String, granted: Boolean)
    }

    private var permissionCallback: PermissionCallback? = null

    private val requestPermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.forEach { (permission, granted) ->
                permissionCallback?.onPermissionResult(permission, granted)
            }
        }

    fun checkAndRequestPermissionsIndividually(
        permissions: Array<String>,
        rationalMessages: Map<String, String>,
        callback: PermissionCallback,
    ) {
        this.permissionCallback = callback

        val context = fragment.requireContext()

        val neededPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (neededPermissions.isEmpty()) {
            permissions.forEach { callback.onPermissionResult(it, true) }
            return
        }

        val permissionToShowRationale = neededPermissions.filter {
            fragment.shouldShowRequestPermissionRationale(it)
        }

        if (permissionToShowRationale.isNotEmpty()) {
            showRationaleDialog(permissionToShowRationale, rationalMessages) {
                requestPermissionLauncher.launch(neededPermissions.toTypedArray())
            }
        } else {
            requestPermissionLauncher.launch(neededPermissions.toTypedArray())
        }
    }

    private fun showRationaleDialog(
        permissions: List<String>,
        rationalMessages: Map<String, String>,
        onAllow: () -> Unit,
    ) {
        val message = permissions.joinToString("\n") {
            "- ${rationalMessages[it] ?: "This permission is required"}"
        }

        AlertDialog.Builder(fragment.requireContext())
            .setTitle("Permission Needed")
            .setMessage(message)
            .setPositiveButton("Allow") { _, _ ->
                onAllow()
            }
            .setNegativeButton("Cancel") { _, _ ->
                permissions.forEach { permissionCallback?.onPermissionResult(it, false) }
            }
            .show()
    }


    fun openSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:${context.packageName}".toUri()
        }
        context.startActivity(intent)
    }

}
package com.miassolutions.rollcall.utils

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.miassolutions.rollcall.extenstions.toFormattedDate
import java.io.File

class StudentImagePicker(
    private val fragment: Fragment,
    private val onImagePicked: (Uri) -> Unit,
) {

    private lateinit var cameraImageUri: Uri
    private val context: Context get() = fragment.requireContext()

    private val galleryLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                onImagePicked(it)
            }
        }

    private val cameraLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) onImagePicked(cameraImageUri)
        }

    private val permissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val permanentDenied = result.filterValues { !it }
                .map { it.key }
                .any { permission ->
                    !fragment.shouldShowRequestPermissionRationale(permission)
                }

            val allGranted = result.all { it.value }

            when {
                allGranted -> showImagePickerOptions()
                permanentDenied -> showPermissionDeniedDialog()
                else -> showRationaleDialog()
            }
        }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(context)
            .setTitle("Permission Denied")
            .setMessage("You have permanently denied required permissions. Please enable them from app settings")
            .setPositiveButton("Open Settings") { _, _ -> openSettings() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:${context.packageName}".toUri()
        }
        context.startActivity(intent)
    }

    private fun showRationaleDialog() {
        AlertDialog.Builder(context)
            .setTitle("Permissions Required")
            .setMessage("Camera and Storage permissions are required to select a photo.")
            .setPositiveButton("Grant") { _, _ -> requestAndPickImage() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun requestAndPickImage() {

        // Create a mutable list to hold the permissions we need.
        val permissions = mutableListOf<String>()

        // Check the Android version to determine the appropriate storage permission.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 (Tiramisu) and above, use READ_MEDIA_IMAGES for more granular access.
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            // For older Android versions, use READ_EXTERNAL_STORAGE.
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // Always add the CAMERA permission, as we might want to take a new photo.
        permissions.add(Manifest.permission.CAMERA)


        // Filter out the permissions that are NOT currently granted.
        // 'denied' will contain only the permissions that the app doesn't have yet.
        val denied = permissions.filterNot {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        // If the 'denied' list is empty, it means all required permissions are already granted.
        if (denied.isEmpty()) {
            // Proceed to show the image picker options (e.g., gallery or camera).
            showImagePickerOptions()
        } else {
            // If there are denied permissions, launch the permission request dialog.
            // 'permissionLauncher' is an ActivityResultLauncher for requesting multiple permissions.
            permissionLauncher.launch(denied.toTypedArray()) //launcher accepted array so we convert mutable list to array
        }

    }

    private fun showImagePickerOptions() {
        val options = arrayOf("Camera", "Gallery")

        AlertDialog.Builder(context)
            .setTitle("Select Image from")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()


    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun openCamera() {
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "IMG_${System.currentTimeMillis().toFormattedDate("dd_MM_yyyy_HH_mm_ss")}.jpg"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        cameraImageUri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) ?: return

        cameraLauncher.launch(cameraImageUri)
    }


}
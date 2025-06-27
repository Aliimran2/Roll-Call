package com.miassolutions.rollcall.extenstions

import android.Manifest
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.miassolutions.rollcall.R
import com.miassolutions.rollcall.ui.MainActivity


fun Fragment.addMenu(
    @MenuRes menuRes: Int,
    onItemSelected: (MenuItem) -> Boolean,
) {
    val menuHost: MenuHost = requireActivity()
    menuHost.addMenuProvider(object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(menuRes, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return onItemSelected(menuItem)
        }
    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
}


fun Fragment.showConfirmationDialog(title: String, message: String, onConfirm: () -> Unit) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Yes") { _, _ ->
            onConfirm()
        }
        .setNegativeButton("Cancel", null)
        .show()
}


fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
) {

    view?.let { hostView ->
        Snackbar.make(hostView, message, duration).show()
    }
}

fun Fragment.showPopupMenu(
    anchorView: View,
    @MenuRes menuRes: Int,
    onMenuItemClickListener: ((MenuItem) -> Boolean)? = null,
) {
    val popupMenu = PopupMenu(requireContext(), anchorView)
    popupMenu.menuInflater.inflate(menuRes, popupMenu.menu)

    onMenuItemClickListener?.let {
        popupMenu.setOnMenuItemClickListener(it)
    }

    popupMenu.show()

}


fun Fragment.setToolbarTitle(title: String) {
    (activity as? AppCompatActivity)?.supportActionBar?.title = title
}

fun Fragment.requestSmsPermission(onGranted: () -> Unit) {
    if (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        onGranted()

    } else {
        requestPermissions(arrayOf(Manifest.permission.SEND_SMS), 101)
    }
}
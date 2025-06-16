package com.miassolutions.rollcall.utils

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Fragment.showToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun LifecycleOwner.collectLatestFlow(
    lifecycleState: Lifecycle.State = Lifecycle.State.STARTED,
    block: suspend CoroutineScope.() -> Unit,
) {
    lifecycleScope.launch {
        repeatOnLifecycle(lifecycleState) { block() }
    }
}

fun Fragment.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
) {

    view?.let { hostView ->
        Snackbar.make(hostView, message, duration).show()
    }
}


fun getCurrentDateAndTime(): Long {
    val calendar = Calendar.getInstance()
    return calendar.time.time

}


fun Long.toFormattedDate(
    pattern: String = "dd/MM/yyyy",
    locale: Locale = Locale.getDefault(),
): String {
    return SimpleDateFormat(pattern, locale).format(Date(this))
}

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

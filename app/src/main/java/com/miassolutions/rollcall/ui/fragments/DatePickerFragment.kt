package com.miassolutions.rollcall.ui.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment

class DatePickerFragment(
    private val onDateSelected: (String) -> Unit
) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)




        val datePicker = DatePickerDialog(requireContext(), this, year, month, day)
        datePicker.datePicker.maxDate = calendar.timeInMillis
        return datePicker
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        val formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
        onDateSelected(formattedDate)


    }
}
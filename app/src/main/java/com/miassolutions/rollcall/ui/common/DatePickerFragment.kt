package com.miassolutions.rollcall.ui.common

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.miassolutions.rollcall.ui.screens.userprofilescreen.UserProfileViewModel
import com.miassolutions.rollcall.common.Constants.DATE_REQUEST_KEY
import com.miassolutions.rollcall.common.Constants.SELECTED_DATE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val viewModel by viewModels<UserProfileViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val calendar = Calendar.getInstance()
        val initYear = calendar.get(Calendar.YEAR)
        val initMonth = calendar.get(Calendar.MONTH)
        val initDay = calendar.get(Calendar.DAY_OF_MONTH)


        val datePickerDialog =
            DatePickerDialog(requireContext(), this, initYear, initMonth, initDay)
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
//        collectLatestFlow {
//            viewModel.minDate.collect { minDate ->
//                minDate?.let {
//                    datePickerDialog.datePicker.minDate = it
//
//                }
//            }
//        }
        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

        val selectedDate = Calendar.getInstance().apply {
            set(year, month, dayOfMonth)
        }

        val date = selectedDate.time.time
        parentFragmentManager.setFragmentResult(DATE_REQUEST_KEY, bundleOf(SELECTED_DATE to date))
    }


}
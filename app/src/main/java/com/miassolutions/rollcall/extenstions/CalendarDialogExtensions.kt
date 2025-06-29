package com.miassolutions.rollcall.extenstions

import WeekendPastDateValidatorUtil
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar

fun Fragment.showMaterialDatePicker(
    title: String,
    inputMode: Int = MaterialDatePicker.INPUT_MODE_CALENDAR,
    selection: Long? = null,
    constraints: CalendarConstraints? = null,
    onDateSelected: (Long) -> Unit,
) {

//    val today = MaterialDatePicker.todayInUtcMilliseconds()

    val builder = MaterialDatePicker.Builder.datePicker()
        .setTitleText(title)
        .setInputMode(inputMode)

    selection?.let {
        builder.setSelection(it)
    }

//        .setSelection(today)

    constraints?.let {
        builder.setCalendarConstraints(constraints)
    }

    val datePicker = builder.build()

    datePicker.addOnPositiveButtonClickListener {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = it
            clearTimeComponents()
        }
        onDateSelected(calendar.timeInMillis)
    }

    datePicker.show(parentFragmentManager, datePicker.tag)

}

fun Calendar.clearTimeComponents() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}

fun Fragment.showAttendanceDatePicker(onDateSelected: (Long) -> Unit) {
    val constraints = CalendarConstraints.Builder()
        .setFirstDayOfWeek(Calendar.MONDAY)
        .setValidator(WeekendPastDateValidatorUtil())
        .build()

    val picker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select Attendance Date")
        .setCalendarConstraints(constraints)
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()

    picker.addOnPositiveButtonClickListener { selectedDate ->
        // Only called when a valid date is selected
        onDateSelected(selectedDate)
    }

    picker.addOnNegativeButtonClickListener {
        // User clicked cancel
    }

    picker.addOnDismissListener {
        // Dialog was dismissed without selection
    }

    picker.show(parentFragmentManager, "DATE_PICKER")
}
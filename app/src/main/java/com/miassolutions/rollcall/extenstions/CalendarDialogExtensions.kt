package com.miassolutions.rollcall.extenstions

import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.ZoneId
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

    selection?.let { builder.setSelection(it) }
    constraints?.let { builder.setCalendarConstraints(constraints) }

    val datePicker = builder.build()

    datePicker.addOnPositiveButtonClickListener { selectedMillis ->
        // Normalize to start of day using java.time
        val selectedDate = Instant.ofEpochMilli(selectedMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val startOfDayMillis = selectedDate //e.g. 2025-07-02T00:00:00.000 in local time
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        // Convert back to millis at start of day
        onDateSelected(startOfDayMillis)

//        val calendar = Calendar.getInstance().apply {
//            timeInMillis = it
//            clearTimeComponents()
//        }
//        onDateSelected(calendar.timeInMillis)
    }

    datePicker.show(parentFragmentManager, datePicker.tag)

}

fun Calendar.clearTimeComponents() {
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}
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



    val builder = MaterialDatePicker.Builder.datePicker()
        .setTitleText(title)

        .setInputMode(inputMode)

    selection?.let { builder.setSelection(it) }
    constraints?.let { builder.setCalendarConstraints(constraints) }

    val datePicker = builder.build()

    datePicker.addOnPositiveButtonClickListener { selectedMillis ->

        val selectedDate = Instant.ofEpochMilli(selectedMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val startOfDayMillis = selectedDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        onDateSelected(startOfDayMillis)


    }

    datePicker.show(parentFragmentManager, datePicker.tag)

}


package com.miassolutions.rollcall.utils

import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import java.util.Calendar
import java.util.Locale

// Extension function to show a MaterialDatePicker with common setup
// and a custom constraints builder callback
fun FragmentManager.showMaterialDatePicker(
    tag: String,
    titleText: String,
    initialSelection: Long? = MaterialDatePicker.todayInUtcMilliseconds(),
    // Lambda to configure CalendarConstraints.Builder
    constraintsBuilder: (CalendarConstraints.Builder.() -> Unit)? = null,
    // Callback for positive button click
    onPositiveButtonClick: MaterialPickerOnPositiveButtonClickListener<Long>
) {
    val builder = MaterialDatePicker.Builder.datePicker()
        .setTitleText(titleText)
        .setSelection(initialSelection)

    // Apply custom constraints if provided
    constraintsBuilder?.let {
        val constraints = CalendarConstraints.Builder().apply(it).build()
        builder.setCalendarConstraints(constraints)
    }

    val datePicker = builder.build()

    datePicker.addOnPositiveButtonClickListener(onPositiveButtonClick)

    datePicker.show(this, tag)
}

// ---- Pre-defined Constraint Builders (for easier use) ----

// Extension for CalendarConstraints.Builder to set first day of week to Monday
fun CalendarConstraints.Builder.setFirstDayOfWeekToMonday() {
    setFirstDayOfWeek(Calendar.MONDAY)
}

// Extension for CalendarConstraints.Builder to disable weekends (Saturday and Sunday)
fun CalendarConstraints.Builder.disableWeekends() {
    setValidator(object : CalendarConstraints.DateValidator {
        override fun isValid(date: Long): Boolean {
            val calendar = Calendar.getInstance(Locale.getDefault())
            calendar.timeInMillis = date
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            return dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY
        }

        // Must override describeContents() and writeToParcel() for Parcelable
        override fun describeContents(): Int = 0
        override fun writeToParcel(dest: android.os.Parcel, flags: Int) {}
    })
}

// Extension for CalendarConstraints.Builder to disable past dates from a certain date
fun CalendarConstraints.Builder.disablePastDatesFrom(minDateMillis: Long) {
    setStart(minDateMillis) // Set the earliest selectable date
}

// Extension for CalendarConstraints.Builder to disable future dates from a certain date
fun CalendarConstraints.Builder.disableFutureDatesFrom(maxDateMillis: Long) {
    setEnd(maxDateMillis) // Set the latest selectable date
}
package com.miassolutions.rollcall.extenstions

import android.widget.AutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText



fun TextInputEditText.setTextIfChanged(newText: String) {
    if (text?.toString() != newText) {
        setText(newText)
        setSelection(newText.length) // keep cursor at end
    }
}

fun AutoCompleteTextView.setTextIfChanged(text: String) {
    if (this.text?.toString() != text) {
        setText(text, false) // prevent dropdown from triggering
        setSelection(text.length)
    }
}


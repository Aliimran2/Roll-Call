package com.miassolutions.rollcall.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class BFormTextWatcher(private val editText: EditText) : TextWatcher {

    private var isFormatting = false
    private var previousText = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        previousText = s.toString()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isFormatting) return

        isFormatting = true

        val digits = s.toString().replace("-", "").take(13) // limit to 13 digits
        val formatted = StringBuilder()

        for (i in digits.indices) {
            formatted.append(digits[i])
            if (i == 4 || i == 11) formatted.append("-")
        }

        editText.removeTextChangedListener(this)
        editText.setText(formatted.toString())
        editText.setSelection(formatted.length.coerceAtMost(editText.text.length))
        editText.addTextChangedListener(this)

        isFormatting = false
    }
}

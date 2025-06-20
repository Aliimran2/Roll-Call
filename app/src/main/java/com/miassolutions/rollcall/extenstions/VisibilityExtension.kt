package com.miassolutions.rollcall.extenstions

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.android.material.appbar.MaterialToolbar

fun View.hide() {
    this.visibility = GONE
}

fun View.show() {
    this.visibility = VISIBLE
}


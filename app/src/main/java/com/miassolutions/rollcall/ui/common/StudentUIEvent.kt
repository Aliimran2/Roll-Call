package com.miassolutions.rollcall.ui.common

sealed class StudentUIEvent {
    data class ShowToast(val message : String) : StudentUIEvent()
    data object NavigateToEditStudent : StudentUIEvent()
    data object NavigateBack : StudentUIEvent()
}
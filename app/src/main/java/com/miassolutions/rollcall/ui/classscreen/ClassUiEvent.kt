package com.miassolutions.rollcall.ui.classscreen

sealed class ClassUiEvent {

    data class ShowToast(val message: String) : ClassUiEvent()
    data object NavigateToEditClass : ClassUiEvent()
    data object NavigateToBack : ClassUiEvent()

}
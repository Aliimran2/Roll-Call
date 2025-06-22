package com.miassolutions.rollcall.ui.uicommon

sealed class ClassUiEvent {

    data class ShowToast(val message: String) : ClassUiState()
    data object NavigateToEditClass : ClassUiState()
    data object NavigateToBack : ClassUiState()

}
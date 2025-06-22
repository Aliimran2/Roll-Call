package com.miassolutions.rollcall.ui.uicommon

import com.miassolutions.rollcall.data.entities.ClassEntity

sealed class ClassUiState {
     data object Empty : ClassUiState()
     data object Loading : ClassUiState()
    data class Success(val classList: List<ClassEntity>) : ClassUiState()
    data class Failure(val message: String) : ClassUiState()

}
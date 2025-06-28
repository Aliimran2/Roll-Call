package com.miassolutions.rollcall.ui.screens.classscreen

import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.ui.model.ClassWithStudents

sealed class ClassUiState {
    data object Empty : ClassUiState()
    data object Loading : ClassUiState()
    data class ClassDetail(val classEntity: ClassEntity?) : ClassUiState()
    data class Success(val classList: List<ClassWithStudents>) : ClassUiState() {
        val totalClasses: Int
            get() = classList.size

    }

    data class Failure(val message: String) : ClassUiState()

}
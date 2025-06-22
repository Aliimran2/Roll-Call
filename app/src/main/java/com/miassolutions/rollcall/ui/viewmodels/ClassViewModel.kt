package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassViewModel @Inject constructor(private val repository: Repository) : ViewModel(){

    val allClasses = repository.getClasses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _toastClassMessage = MutableSharedFlow<String>()
    val toastClassMessage = _toastClassMessage.asSharedFlow()


    fun insertClass(classEntity: ClassEntity){
        viewModelScope.launch {
            repository.insertClass(classEntity)
//            _toastClassMessage.emit()
        }
    }

    fun deleteClass(classEntity: ClassEntity){
        viewModelScope.launch {
            repository.deleteClass(classEntity)
        }
    }

    fun updateClass(classEntity: ClassEntity){
        viewModelScope.launch {
            repository.updateClass(classEntity)
        }
    }

}
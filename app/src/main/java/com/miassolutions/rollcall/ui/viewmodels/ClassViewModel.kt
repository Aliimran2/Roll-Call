package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.ui.screens.classscreen.ClassUiEvent
import com.miassolutions.rollcall.ui.screens.classscreen.ClassUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassViewModel @Inject constructor(private val repository: Repository) : ViewModel() {


    private val _uiState = MutableStateFlow<ClassUiState>(ClassUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<ClassUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        observeClasses()
    }

    private fun observeClasses() {
        viewModelScope.launch {
            repository.getClasses()
                .catch { e ->
                    _uiState.value = ClassUiState.Failure("Error : ${e.localizedMessage}")
                }
                .collectLatest { list ->
                    _uiState.value = if (list.isEmpty()) {
                        ClassUiState.Empty
                    } else {
                        ClassUiState.Success(list)
                    }
                }
        }
    }

    fun copyClass(classEntity: ClassEntity) {
        viewModelScope.launch {
            repository.copyClass(classEntity)
        }
    }

    fun insertClass(classEntity: ClassEntity) {
        viewModelScope.launch {
            try {
                repository.insertClass(classEntity)

                _uiEvent.send(ClassUiEvent.ShowToast("Class added successfully"))
            } catch (e: Exception) {
                _uiEvent.send(ClassUiEvent.ShowToast("Failed to insert : ${e.localizedMessage}"))
            }
        }
    }

    fun updateClass(classEntity: ClassEntity) {
        viewModelScope.launch {
            try {
                repository.updateClass(classEntity)
                _uiEvent.send(ClassUiEvent.ShowToast("Class updated successfully"))
            } catch (e: Exception) {
                _uiEvent.send(ClassUiEvent.ShowToast("Failed to update : ${e.localizedMessage}"))
            }
        }
    }

    fun deleteClass(classEntity: ClassEntity) {
        viewModelScope.launch {
            try {
                repository.deleteClass(classEntity)
                _uiEvent.send(ClassUiEvent.ShowToast("${classEntity.className} is deleted"))
            } catch (e: Exception) {
                _uiEvent.send(ClassUiEvent.ShowToast("Failed to delete class : ${e.localizedMessage}"))
            }
        }
    }

}
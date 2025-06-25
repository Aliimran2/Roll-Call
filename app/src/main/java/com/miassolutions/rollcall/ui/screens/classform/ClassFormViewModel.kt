package com.miassolutions.rollcall.ui.screens.classform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.ClassEntity
import com.miassolutions.rollcall.data.repository.impl.ClassRepoImpl
import com.miassolutions.rollcall.extenstions.toFormattedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ClassFormViewModel @Inject constructor(private val repository: ClassRepoImpl) : ViewModel() {


    private val _uiState = MutableStateFlow(ClassFormUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<ClassFormUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun loadClassForEdit(id: String) {
        viewModelScope.launch {
            repository.getClassById(id).collectLatest { classEntity ->
                classEntity?.let {
                    _uiState.update { state ->
                        state.copy(
                            classId = it.classId,
                            className = it.className,
                            sectionName = it.sectionName ?: "",
                            startDateStr = it.startDate.toFormattedDate(),
                            endDateStr = it.endDate.toFormattedDate(),
                            teacherName = it.teacher,
                            isEditMode = true
                        )
                    }
                }

            }


        }
    }

    fun onClassNameChange(className: String) {
        _uiState.update { it.copy(className = className) }
    }

    fun onSectionNameChange(sectionName: String) {
        _uiState.update { it.copy(sectionName = sectionName) }
    }

    fun onTeacherNameChange(teacherName: String) {
        _uiState.update { it.copy(teacherName = teacherName) }
    }

    fun onStartDateChange(value: String) {
        _uiState.update { it.copy(startDateStr = value) }
    }

    fun onEndDateChange(value: String) {
        _uiState.update { it.copy(endDateStr = value) }
    }

    fun onSavClicked() {
        val state = _uiState.value

        if (state.className.isBlank() ||
            state.sectionName.isBlank() ||
            state.teacherName.isBlank() ||
            state.startDateStr.isBlank() ||
            state.endDateStr.isBlank()
        ) {
            viewModelScope.launch {
                _uiEvent.send(ClassFormUiEvent.ShowToast("These all fields are required"))
            }
            return
        }



        val classEntity = ClassEntity(
            classId = state.classId ?: UUID.randomUUID().toString(),
            className = state.className,
            sectionName = state.sectionName,
            startDate = state.startDate,
            endDate = state.endDate,
            teacher = state.teacherName,

            )

        viewModelScope.launch {
            if (state.isEditMode) {
                repository.updateClass(classEntity)
                _uiEvent.send(ClassFormUiEvent.ShowToast("Class updated"))
            } else {
                repository.insertClass(classEntity)
                _uiEvent.send(ClassFormUiEvent.ShowToast("Class added!"))
            }
            _uiEvent.send(ClassFormUiEvent.NavigateBack)
        }
    }

}
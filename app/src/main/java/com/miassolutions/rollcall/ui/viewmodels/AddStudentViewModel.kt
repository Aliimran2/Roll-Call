package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.utils.StudentInsertResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddStudentViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val allStudents: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _toastMessage = MutableSharedFlow<StudentInsertResult>()
    val toastMessage = _toastMessage.asSharedFlow()


    fun insertStudent(student: Student) {
        viewModelScope.launch {

            val result = repository.insertStudent(student)
            withContext(Dispatchers.Main) {

                _toastMessage.emit(result)
            }
        }
    }


    private val _importUIState = MutableStateFlow<ImportUIState>(ImportUIState.Idle)
    val importUIState = _importUIState.asStateFlow()

    sealed class ImportUIState {
        data object Idle : ImportUIState()
        data object Importing : ImportUIState()
        data class Success(val successCount: Int, val failureCount: Int) : ImportUIState()
        data class Error(val message: String) : ImportUIState()
    }

    fun importStudents(students: List<Student>){
        viewModelScope.launch {
            _importUIState.value = ImportUIState.Importing
            var successCount = 0
            var failureCount = 0

            students.forEach {student->
                when(repository.insertStudent(student)){
                    is StudentInsertResult.Failure -> failureCount++
                    is StudentInsertResult.Success -> successCount++
                }
            }

            _importUIState.value = ImportUIState.Success(successCount, failureCount)
            delay(1000)
            _importUIState.value = ImportUIState.Idle
        }
    }

}
package com.miassolutions.rollcall.ui.screens.studentformscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.common.InsertResult
import com.miassolutions.rollcall.common.OperationResult
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.data.repository.impl.StudentRepoImpl
import com.miassolutions.rollcall.utils.StudentInsertResult
import com.miassolutions.rollcall.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentFormViewModel @Inject constructor(private val repository: StudentRepoImpl) : ViewModel() {

    private val _studentToEdit = MutableStateFlow<StudentEntity?>(null)
    val studentToEdit = _studentToEdit.asStateFlow()

    private val _toastMessage = MutableSharedFlow<InsertResult>()
    val toastMessage = _toastMessage.asSharedFlow()


    fun fetchStudentById(studentId: String) {
        viewModelScope.launch {
            try {
                when (val result = repository.getStudentById(studentId)) {

                    is OperationResult.Success<StudentEntity> -> {
                        _studentToEdit.value = result.data
                    }

                    is OperationResult.Error -> {
                        _toastMessage.emit(InsertResult.Failure(result.message))
                    }

                    OperationResult.Loading -> {}

                }
            } catch (e: Exception) {
                _toastMessage.emit(InsertResult.Failure("failed"))

            }
        }
    }

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.updateStudent(student); _toastMessage.emit(
            InsertResult.Success
        )
        }
    }

    fun insertStudent(studentEntity: StudentEntity) {
        viewModelScope.launch {
            val result = repository.insertStudent(studentEntity)
            _toastMessage.emit(result)
        }
    }


}
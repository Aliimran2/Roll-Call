package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.utils.StudentInsertResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddStudentViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val allStudents: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()


    fun insertStudent(student: Student) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                repository.insertStudent(student)
            }

            when (result) {
                is StudentInsertResult.Duplicate -> {
                    _toastMessage.emit("Duplicated")
                }

                is StudentInsertResult.Error -> {
                    _toastMessage.emit("Something went wrong")
                }

                is StudentInsertResult.Success -> {
                    _toastMessage.emit("Student added in db")
                }
            }

        }
    }
}
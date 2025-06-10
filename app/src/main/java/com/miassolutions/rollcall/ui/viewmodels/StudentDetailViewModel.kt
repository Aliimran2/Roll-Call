package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.data.repository.StudentFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentDetailViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _studentState =
        MutableStateFlow<StudentFetchResult<Student>>(StudentFetchResult.Loading)
    val studentState: StateFlow<StudentFetchResult<Student>> = _studentState

    private val _deleteMessage = MutableSharedFlow<String>()
    val deleteMessage = _deleteMessage.asSharedFlow()


    fun fetchStudentById(studentId: String) {
        viewModelScope.launch {

            _studentState.value = StudentFetchResult.Loading
            _studentState.value = repository.getStudentById(studentId)
                _deleteMessage.emit("Deleted")


        }
    }



    fun deleteStudentById(studentId: String) {
        viewModelScope.launch {

            repository.deleteStudentById(studentId)
        }
    }


}
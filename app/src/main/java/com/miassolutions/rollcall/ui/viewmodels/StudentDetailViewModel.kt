package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.Student
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.data.repository.StudentFetchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentDetailViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _studentState =
        MutableStateFlow<StudentFetchResult<Student>>(StudentFetchResult.Loading)
    val studentState: StateFlow<StudentFetchResult<Student>> = _studentState


    fun fetchStudentById(studentId: String) {
        viewModelScope.launch {
            _studentState.value = StudentFetchResult.Loading
            _studentState.value = repository.getStudentById(studentId)
        }
    }


}
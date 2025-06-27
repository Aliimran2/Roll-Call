package com.miassolutions.rollcall.ui.screens.studentlistscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.data.repository.impl.StudentRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentListViewModel @Inject constructor(private val studentRepo: StudentRepoImpl) :
    ViewModel() {


    private val searchQuery = MutableStateFlow("")
    private val mClassId = MutableStateFlow("")


    private val _uiState = MutableStateFlow(StudentListUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<StudentListUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var classId: String = ""
    private var className: String = ""

    fun updateClassId(id: String, name: String) {
        classId = id
        className = name

        fetchStudents()
    }

    private fun fetchStudents() {
        viewModelScope.launch {
            studentRepo.getStudentListByClassId(classId).collectLatest { students ->
                _uiState.update { it.copy(studentList = students) }
            }
        }
    }

    fun onSearchQueryUpdate(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filterStudents(query)
    }

    private fun filterStudents(query: String) {
        viewModelScope.launch {
            studentRepo.getStudentListByClassId(classId).collectLatest { all ->
                val filtered = if (query.isBlank()) all
                else all.filter {
                    it.studentName.contains(query, ignoreCase = true) ||
                            it.rollNumber.toString().contains(query, ignoreCase = true)
                }
                _uiState.update { it.copy(studentList = filtered) }
            }
        }

    }

    fun onAddStudentClicked() {
        viewModelScope.launch {
            _uiEvent.emit(StudentListUiEvent.NavigateToAddOrEdit(null, classId, className))
        }
    }

    fun onUpdateStudentClicked(studentId: String) {
        viewModelScope.launch {
            _uiEvent.emit(StudentListUiEvent.NavigateToAddOrEdit(studentId, classId, className))
        }
    }

    fun onStudentClicked(student : StudentEntity){
        viewModelScope.launch {
            _uiEvent.emit(StudentListUiEvent.NavigateToStudentDetail(student.studentId, student.studentName))
        }
    }

    fun onDeleteClicked(studentId: String){
        viewModelScope.launch {
            _uiEvent.emit(StudentListUiEvent.ShowDeleteConfirmation(studentId))
        }
    }

    fun deleteStudent(studentId: String){
        viewModelScope.launch {
            studentRepo.deleteStudentById(studentId)
            _uiEvent.emit(StudentListUiEvent.ShowSnackbar("Student deleted"))
        }
    }

    fun onPhoneClicked(phone : String){
        viewModelScope.launch {
            _uiEvent.emit(StudentListUiEvent.DialPhone(phone))
        }
    }

    fun onReportClicked(studentId: String){
        viewModelScope.launch {
            _uiEvent.emit(StudentListUiEvent.ShowSnackbar("Later will be implemented"))
        }
    }


    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }
//
//    fun updateClassId(classId: String) {
//        mClassId.value = classId
//    }

    fun deleteStudentById(studentId: String) {
        viewModelScope.launch {
            studentRepo.deleteStudentById(studentId)
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val filteredStudents = searchQuery
        .debounce(200L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                studentRepo.getStudentListByClassId(classId = mClassId.value)
            } else {
                studentRepo.searchStudents(query)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


}



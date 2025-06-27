package com.miassolutions.rollcall.ui.screens.studentlistscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.repository.impl.StudentRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentListViewModel @Inject constructor(private val studentRepo: StudentRepoImpl) :
    ViewModel() {


    private val searchQuery = MutableStateFlow("")
    private val mClassId = MutableStateFlow("")

    private val _uiState = MutableStateFlow<StudentListUiState>(StudentListUiState.Empty)
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<StudentListUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()





    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun updateClassId(classId: String) {
        mClassId.value = classId
    }

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



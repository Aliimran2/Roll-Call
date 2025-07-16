package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.utils.StudentInsertResult
import com.miassolutions.rollcall.utils.StudentResult
import com.miassolutions.rollcall.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AddStudentViewModel @Inject constructor(private val repository: Repository) : ViewModel() {


    private val _searchQuery = MutableStateFlow<String>("")

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private val _noOfTotalStudents = MutableStateFlow<Int>(0)
    val noOfTotalStudents: StateFlow<Int> = _noOfTotalStudents.asStateFlow()

    val filteredStudents: StateFlow<List<StudentEntity>> = _searchQuery
        .debounce(200L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allStudentsFlow
            } else {
                repository.searchStudents(query) // Perform search with the given query
            }
        }
        .onEach {
            _noOfTotalStudents.value = it.size
        }
        /*
        *This converts the Flow into a StateFlow, making it suitable for UI observation with features
        * State retention: Keeps the last emitted value
        * Hot behavior: Starts emitting as long as there's a subscriber
         */

        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    private val _studentToEdit = MutableStateFlow<StudentEntity?>(null)
    val studentToEdit = _studentToEdit.asStateFlow()

    private val _toastMessage = MutableSharedFlow<StudentInsertResult>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val _importUIState = MutableStateFlow<UiState<Pair<Int, Int>>>(UiState.Idle)
    val importUIState = _importUIState.asStateFlow()

    fun fetchStudentById(studentId: String) {
        viewModelScope.launch {
            try {
                when (val result = repository.getStudentById(studentId)) {

                    is StudentResult.Success<StudentEntity> -> {
                        _studentToEdit.value = result.data
                    }

                    is StudentResult.Error -> {
                        _toastMessage.emit(StudentInsertResult.Failure(result.message))
                    }

                    StudentResult.Loading -> {}

                }
            } catch (e: Exception) {
                _toastMessage.emit(StudentInsertResult.Failure("failed"))

            }
        }
    }

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch {
            repository.updateStudent(student); _toastMessage.emit(
            StudentInsertResult.Success
        )
        }
    }

    fun insertStudent(studentEntity: StudentEntity) {
        viewModelScope.launch {
            val result = repository.insertStudent(studentEntity)
            _toastMessage.emit(result)
        }
    }


    fun importStudents(studentEntities: List<StudentEntity>) {
        viewModelScope.launch {
            _importUIState.value = UiState.Loading
            val (success, failure) = repository.insertStudentsBulk(studentEntities)
            _importUIState.value = UiState.Success(success to failure)
            delay(1000)
            _importUIState.value = UiState.Idle
        }
    }

}
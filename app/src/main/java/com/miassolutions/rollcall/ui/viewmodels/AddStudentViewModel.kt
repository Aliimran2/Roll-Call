package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.data.repository.StudentFetchResult
import com.miassolutions.rollcall.utils.StudentInsertResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AddStudentViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    // Internal mutable StateFlow for the search query
    private val _searchQuery = MutableStateFlow<String>("")

    //total students
    private val _totalStudents = MutableStateFlow<Int>(0)
    val totalStudents : StateFlow<Int> = _totalStudents.asStateFlow()

    // Public StateFlow to expose the filtered list of students to the UI
    // Naming changed from 'allStudents' to 'filteredStudents' for clarity
    val filteredStudents: StateFlow<List<StudentEntity>> = _searchQuery
        .debounce(300L)          // Wait for 300ms of inactivity before processing
        .distinctUntilChanged()  // Only proceed if the query is different from the previous one
        .flatMapLatest { query -> // Cancel previous search and start a new one if query changes
            if (query.isBlank()) {
                repository.allStudents // Get all students if query is empty or just whitespace
            } else {
                repository.searchStudents(query) // Perform search with the given query
            }
        }
        .onEach {
            _totalStudents.value = it.size
        }
        // Convert the Flow to a StateFlow, sharing the underlying data
        // Starts collection when there's at least one subscriber, keeps active for 5s after last subscriber leaves
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    /**
     * Updates the current search query. This function should be called from the UI
     * whenever the text in the SearchView changes.
     */
    // Function name changed from 'searchQuery' to 'onSearchQueryChanged' for clarity
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }






    private val _studentToEdit = MutableStateFlow<StudentEntity?>(null)
    val studentToEdit = _studentToEdit.asStateFlow()

    fun fetchStudentById(studentId: String) {
        viewModelScope.launch {
            try {
                when (val student = repository.getStudentById(studentId)) {
                    is StudentFetchResult.Error -> {}
                    StudentFetchResult.Loading -> {}
                    is StudentFetchResult.Success<StudentEntity> -> {
                        _studentToEdit.value = student.data
                    }
                }
            } catch (e: Exception) {
                _toastMessage.emit(StudentInsertResult.Failure("failed"))

            }
        }
    }

    private val _toastMessage = MutableSharedFlow<StudentInsertResult>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun updateStudent(student: StudentEntity) {
        viewModelScope.launch { repository.updateStudent(student);  _toastMessage.emit(StudentInsertResult.Success) }
    }


    fun insertStudent(studentEntity: StudentEntity) {
        viewModelScope.launch {

            val result = repository.insertStudent(studentEntity)
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

    fun importStudents(studentEntities: List<StudentEntity>) {
        viewModelScope.launch {
            _importUIState.value = ImportUIState.Importing
            var successCount = 0
            var failureCount = 0

            studentEntities.forEach { student ->
                when (repository.insertStudent(student)) {
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
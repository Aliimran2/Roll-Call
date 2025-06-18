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

    private val _allStudents =  MutableStateFlow<List<StudentEntity>>(emptyList())
    val allStudents : StateFlow<List<StudentEntity>> = _allStudents.asStateFlow()

    private val _searchQuery = MutableStateFlow<String>("")
    private val searchQuery : StateFlow<String> = _searchQuery.asStateFlow()

    init {
        searchQuery
            .debounce(300L)
            .flatMapLatest {query ->
                if (query.isEmpty()){
                    repository.allStudents
                } else {
                    repository.searchStudents(query)
                }
            }
            .onEach { studentList ->
                _allStudents.value = studentList
            }
            .launchIn(viewModelScope)
    }

    fun searchQuery(query : String){
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
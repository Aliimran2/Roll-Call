package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.common.OperationResult
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.data.repository.impl.StudentRepoImpl
import com.miassolutions.rollcall.extenstions.toFormattedDate
import com.miassolutions.rollcall.ui.screens.studentdetailscreen.StudentDetailUIState
import com.miassolutions.rollcall.ui.screens.studentdetailscreen.StudentDetailUiEvent
import com.miassolutions.rollcall.ui.screens.studentdetailscreen.PrimaryProfile
import com.miassolutions.rollcall.ui.screens.studentdetailscreen.SecondaryProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class StudentDetailViewModel @Inject constructor(private val repository: StudentRepoImpl) :
    ViewModel() {


    private val _uiState = MutableStateFlow(StudentDetailUIState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<StudentDetailUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var studentId = ""

    fun fetchStudentById(studentId: String) {
        this.studentId = studentId
        loadData()
    }


    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                when (val student = repository.getStudentById(studentId)) {
                    is OperationResult.Error -> {
                        _uiEvent.emit(StudentDetailUiEvent.ShowSnackbar("Student not found"))
                    }

                    OperationResult.Loading -> {}
                    is OperationResult.Success<StudentEntity> -> {
                        val primaryProfile =
                            student.data.studentImage?.let {
                                PrimaryProfile(
                                    name = student.data.studentName,
                                    rollNum = student.data.rollNumber.toString(),
                                    regNum = student.data.regNumber,
                                    imageUri = it,
                                    dateOfBirth = student.data.dob.toFormattedDate(),
                                    dateOfAdmission = student.data.doa?.toFormattedDate() ?: "",
                                    bForm = student.data.bForm ?: "0000-0000000-0",
                                )
                            }

                        val secondaryProfile = SecondaryProfile(
                            fatherName = student.data.fatherName,
                            phoneNumber = student.data.phoneNumber ?: "03000000000",
                            address = student.data.address ?: "Not provided"
                        )
                        _uiState.update {
                            it.copy(
                                primaryProfile = primaryProfile,
                                secondaryProfile = secondaryProfile
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _uiEvent.emit(StudentDetailUiEvent.ShowSnackbar("Error :${e.localizedMessage}"))
            }
        }
    }


//    private val _studentEntityState =
//        MutableStateFlow<StudentResult<StudentEntity>>(StudentResult.Loading)
//    val studentEntityState: StateFlow<StudentResult<StudentEntity>> = _studentEntityState
//
//    private val _deleteMessage = MutableSharedFlow<String>()
//    val deleteMessage = _deleteMessage.asSharedFlow()
//
//
//    fun fetchStudentById(studentId: String) {
//        viewModelScope.launch {
//
//            _studentEntityState.value = StudentResult.Loading
//            _studentEntityState.value = repository.getStudentById(studentId)
//            _deleteMessage.emit("Deleted")
//
//        }
//    }
//
//    private val _studentId = MutableStateFlow("")
//    fun setStudentId(studentId: String) {
//        _studentId.value = studentId
//    }

//    val attendanceOfStudent: StateFlow<List<AttendanceEntity>> = _studentId
//        .filterNotNull()
//        .flatMapLatest { id -> repository.getAttendanceForStudent(id) }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
//
//    val presentCount = attendanceOfStudent
//        .map { list -> list.count() { it.attendanceStatus == AttendanceStatus.PRESENT } }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
//
//    val absentCount = attendanceOfStudent
//        .map { list -> list.count { it.attendanceStatus == AttendanceStatus.ABSENT } }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
//
//    val attendancePercentage = attendanceOfStudent
//        .map { list ->
//            val total = list.size
//            val present = list.count { it.attendanceStatus == AttendanceStatus.PRESENT }
//            if (total == 0) 0 else (present * 100) / total
//        }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
//
//    fun deleteStudentById(studentId: String) {
//        viewModelScope.launch {
//
//            repository.deleteStudentById(studentId)
//        }
//    }
//

}
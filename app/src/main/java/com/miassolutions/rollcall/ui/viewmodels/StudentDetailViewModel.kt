package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.common.AttendanceStatus
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.utils.StudentResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class StudentDetailViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _studentEntityState =
        MutableStateFlow<StudentResult<StudentEntity>>(StudentResult.Loading)
    val studentEntityState: StateFlow<StudentResult<StudentEntity>> = _studentEntityState

    private val _deleteMessage = MutableSharedFlow<String>()
    val deleteMessage = _deleteMessage.asSharedFlow()


    fun fetchStudentById(studentId: String) {
        viewModelScope.launch {

            _studentEntityState.value = StudentResult.Loading
            _studentEntityState.value = repository.getStudentById(studentId)
            _deleteMessage.emit("Deleted")

        }
    }

    private val _studentId = MutableStateFlow("")
    fun setStudentId(studentId: String) {
        _studentId.value = studentId
    }

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
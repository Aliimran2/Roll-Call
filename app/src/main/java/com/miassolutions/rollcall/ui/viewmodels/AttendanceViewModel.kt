package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.ui.model.MarkAttendanceUiModel
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.utils.AttendanceStatus
import com.miassolutions.rollcall.utils.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AttendanceViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val studentList = repository.allStudents.asLiveData()

    fun saveAttendance(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val date = _selectedDate.value
            val alreadyExists = repository.isAttendanceTaken(date)

            if (alreadyExists) {
                onResult(false)
            } else {

                val attendanceEntityList = _uiState.value.map {
                    AttendanceEntity(
                        studentId = it.studentId,
                        date = date,
                        attendanceStatus = it.attendanceStatus
                    )
                }
                repository.insertAttendances(attendanceEntityList)
                onResult(true)
            }


        }
    }

    private val _uiState = MutableStateFlow<List<MarkAttendanceUiModel>>(emptyList())
    val uiState: StateFlow<List<MarkAttendanceUiModel>> = _uiState


    fun setInitialAttendanceList(students: List<MarkAttendanceUiModel>) {
        _uiState.value = students
    }

    // When toggle is changed in the adapter
    fun updateAttendanceStatus(student: MarkAttendanceUiModel, newStatus: AttendanceStatus) {
        _uiState.value = _uiState.value.map {
            if (it.studentId == student.studentId) it.copy(attendanceStatus = newStatus) else it
        }
    }

    // Helper functions
    val totalCount: StateFlow<Int> = uiState.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val presentCount: StateFlow<Int> = uiState.map {
        it.count { student -> student.attendanceStatus == AttendanceStatus.PRESENT }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val absentCount: StateFlow<Int> = uiState.map {
        it.count { student -> student.attendanceStatus == AttendanceStatus.ABSENT }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)


    private val _selectedDate = MutableStateFlow(getCurrentDate())

    fun setDate(date: String) {
        _selectedDate.value = date
    }

}
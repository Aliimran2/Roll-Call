package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.ui.model.AttendanceUIModel
import com.miassolutions.rollcall.utils.AttendanceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    // --- Public State ---

    private val studentList = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDate = MutableStateFlow<Long?>(null)
    val selectedDate: StateFlow<Long?> = _selectedDate

    private val _attendanceUI = MutableStateFlow<List<AttendanceUIModel>>(emptyList())
    val attendanceUI: StateFlow<List<AttendanceUIModel>> = _attendanceUI

    
    // --- Init block to collect studentList + selectedDate ---
    init {
        viewModelScope.launch {
            combine(studentList, selectedDate.filterNotNull()) { students, date ->
                val attendanceEntities = repository.getAttendanceForDate(date)

                students.map { student ->
                    val match = attendanceEntities.find { it.studentId == student.studentId }
                    AttendanceUIModel(
                        studentId = student.studentId,
                        studentName = student.studentName,
                        rollNumber = student.rollNumber,
                        attendanceStatus = match?.attendanceStatus ?: AttendanceStatus.ABSENT
                    )
                }
            }.collectLatest { attendanceModels ->
                _attendanceUI.value = attendanceModels
            }
        }
    }

    // --- Actions ---

    fun setDate(date: Long) {
        _selectedDate.value = date
    }

    fun updateAttendanceStatus(student: AttendanceUIModel, newStatus: AttendanceStatus) {
        _attendanceUI.value = _attendanceUI.value.map {
            if (it.studentId == student.studentId) it.copy(attendanceStatus = newStatus) else it
        }
    }

    fun saveAttendance(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val date = _selectedDate.value ?: return@launch
            val alreadyExists = repository.isAttendanceTaken(date)

            if (alreadyExists) {
                onResult(false)
            } else {
                val entityList = _attendanceUI.value.map {
                    AttendanceEntity(
                        studentId = it.studentId,
                        date = date,
                        attendanceStatus = it.attendanceStatus
                    )
                }
                repository.insertAttendances(entityList)
                onResult(true)
            }
        }
    }

    fun updateAttendanceForDate(date: Long, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.replaceAttendanceForDate(date, _attendanceUI.value.map {
                    AttendanceEntity(
                        studentId = it.studentId,
                        date = date,
                        attendanceStatus = it.attendanceStatus
                    )
                })
                onComplete(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }

    //counts

    val totalCount = attendanceUI.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val presentCount = attendanceUI.map { list ->
        list.count { it.attendanceStatus == AttendanceStatus.PRESENT }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val absentCount = attendanceUI.map { list ->
        list.count { it.attendanceStatus == AttendanceStatus.ABSENT }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}

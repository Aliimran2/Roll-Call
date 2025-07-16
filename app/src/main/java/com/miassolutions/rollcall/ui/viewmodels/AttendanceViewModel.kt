package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.common.AttendanceFilter
import com.miassolutions.rollcall.common.AttendanceStatus
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.ui.model.AttendanceUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    // --- Public State ---

    private val studentList = repository.allStudentsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate

    private val _attendanceUI = MutableStateFlow<List<AttendanceUIModel>>(emptyList())
    val attendanceUI: StateFlow<List<AttendanceUIModel>> = _attendanceUI

    private val _filter = MutableStateFlow(AttendanceFilter.ALL)
    val filter: StateFlow<AttendanceFilter> = _filter.asStateFlow()

    fun setFilter(filter: AttendanceFilter) {
        _filter.value = filter
    }

    private val _searchStudent = MutableStateFlow("")
    private val searchStudent = _searchStudent.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchStudent.value = query
    }


    val filteredAttendanceUI: StateFlow<List<AttendanceUIModel>> = combine(
        _attendanceUI, _filter, _searchStudent
    ) { list, filter, searchStudent ->
        list
            .filter {
                //apply the filter first
                when (filter) {
                    AttendanceFilter.ALL -> true
                    AttendanceFilter.PRESENT -> it.attendanceStatus == AttendanceStatus.PRESENT
                    AttendanceFilter.ABSENT -> it.attendanceStatus == AttendanceStatus.ABSENT
                }
            }
            .filter {
                it.studentName.contains(searchStudent, ignoreCase = true)
                        || it.rollNumber.toString() == searchStudent
            }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


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
                        attendanceStatus = match?.attendanceStatus ?: AttendanceStatus.PRESENT
                    )
                }
            }.collectLatest { attendanceModels ->
                _attendanceUI.value = attendanceModels
            }
        }
    }

    // --- Actions ---

    fun setDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun updateAttendanceStatus(student: AttendanceUIModel, newStatus: AttendanceStatus) {
        _attendanceUI.value = _attendanceUI.value.map {
            if (it.studentId == student.studentId) it.copy(attendanceStatus = newStatus) else it
        }
    }


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
                        attendanceStatus = match?.attendanceStatus ?: AttendanceStatus.PRESENT
                    )
                }
            }.collectLatest { attendanceModels ->
                _attendanceUI.value = attendanceModels
            }
        }
    }

    fun saveAttendance(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val date = _selectedDate.value ?: return@launch
            val alreadyExists = repository.isAttendanceTakenOnce(date)

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

    fun updateAttendanceForDate(date: LocalDate, onComplete: (Boolean) -> Unit) {
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

package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.ui.model.AttendanceUIModel
import com.miassolutions.rollcall.utils.AttendanceStatus
import com.miassolutions.rollcall.utils.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val studentList = repository.allStudents.asLiveData()

    private val _selectedDate = MutableStateFlow(getCurrentDate())


    private val _attendanceUI = MutableStateFlow<List<AttendanceUIModel>>(emptyList())
    val attendanceUI: StateFlow<List<AttendanceUIModel>> = _attendanceUI

    // Counts
    // measure the size of the list
    val totalCount: StateFlow<Int> = attendanceUI.map { attendanceList: List<AttendanceUIModel> ->
        attendanceList.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val presentCount: StateFlow<Int> = attendanceUI.map { attendanceList: List<AttendanceUIModel> ->
        attendanceList.count { student -> student.attendanceStatus == AttendanceStatus.PRESENT }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val absentCount: StateFlow<Int> = attendanceUI.map { attendanceList: List<AttendanceUIModel> ->
        attendanceList.count { student -> student.attendanceStatus == AttendanceStatus.ABSENT }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun saveAttendance(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val date = _selectedDate.value
            val alreadyExists = repository.isAttendanceTaken(date)

            if (alreadyExists) {
                onResult(false)
            } else {

                val attendanceEntityList = _attendanceUI.value.map {
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


    fun setInitialAttendanceList(students: List<AttendanceUIModel>) {
        _attendanceUI.value = students
    }

    // When toggle is changed in the adapter
    fun updateAttendanceStatus(student: AttendanceUIModel, newStatus: AttendanceStatus) {
        _attendanceUI.value = _attendanceUI.value.map {
            if (it.studentId == student.studentId) it.copy(attendanceStatus = newStatus) else it
        }
    }


    fun setDate(date: String) {
        _selectedDate.value = date
    }

}
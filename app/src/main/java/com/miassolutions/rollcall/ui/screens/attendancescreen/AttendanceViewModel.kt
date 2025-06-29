package com.miassolutions.rollcall.ui.screens.attendancescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.common.AttendanceFilter
import com.miassolutions.rollcall.common.AttendanceStatus
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.repository.impl.AttendanceRepoImpl
import com.miassolutions.rollcall.data.repository.impl.StudentRepoImpl
import com.miassolutions.rollcall.extenstions.toFormattedDate
import com.miassolutions.rollcall.ui.model.AttendanceUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepo: AttendanceRepoImpl,
    private val studentRepo: StudentRepoImpl,
) : ViewModel() {

    private val _classId = MutableStateFlow("")
    val classId = _classId.asStateFlow()

    fun setClassId(id: String) {
        _classId.value = id

    }

    private val _date = MutableStateFlow(Date().time)
    val date = _date.asStateFlow()

    fun setDate(date: Long) {
        _date.value = date
    }

    private val studentList = studentRepo.getStudentListByClassId(_classId.value)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    private val _attendanceUI = MutableStateFlow<List<AttendanceUIModel>>(emptyList())
    val attendanceUI = _attendanceUI.asStateFlow()

    private val _filter = MutableStateFlow(AttendanceFilter.ALL)
    val filter = _filter.asStateFlow()

    fun setFilter(filter: AttendanceFilter) {
        _filter.value = filter
    }


    val filteredAttendanceUI: StateFlow<List<AttendanceUIModel>> =
        combine(_attendanceUI, _filter) { list, filter ->
            when (filter) {
                AttendanceFilter.ALL -> list
                AttendanceFilter.PRESENT -> list.filter { it.attendanceStatus == AttendanceStatus.PRESENT }
                AttendanceFilter.ABSENT -> list.filter { it.attendanceStatus == AttendanceStatus.ABSENT }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateAttendanceStatus(student: AttendanceUIModel, newStatus: AttendanceStatus) {
        _attendanceUI.value = _attendanceUI.value.map {
            if (it.studentId == student.studentId) it.copy(attendanceStatus = newStatus) else it
        }
    }


    fun saveAttendance(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val date = _date.value
            val alreadyExist = attendanceRepo.isAttendanceTaken(date)

            if (alreadyExist) {
                onResult(false)
            } else {
                val attendanceList = _attendanceUI.value.map {
                    AttendanceEntity(
                        classId = classId.value,
                        studentId = it.studentId,
                        date = date,
                        attendanceStatus = it.attendanceStatus
                    )
                }
                attendanceRepo.insertAttendances(attendanceList)
                onResult(true)
            }
        }
    }

    fun updateAttendanceForDate(classId: String, date: Long, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                attendanceRepo.replaceAttendanceForDate(classId, date, _attendanceUI.value.map {
                    AttendanceEntity(
                        classId,
                        it.studentId,
                        date = date,
                        attendanceStatus = it.attendanceStatus
                    )
                })


            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }

    val totalCounts = attendanceUI.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val presentCount = attendanceUI.map { list ->
        list.count { it.attendanceStatus == AttendanceStatus.PRESENT }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val absentCount = attendanceUI.map { list ->
        list.count { it.attendanceStatus == AttendanceStatus.ABSENT }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

}

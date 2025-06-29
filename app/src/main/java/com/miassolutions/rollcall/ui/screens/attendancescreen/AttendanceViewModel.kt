package com.miassolutions.rollcall.ui.screens.attendancescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.common.AttendanceFilter
import com.miassolutions.rollcall.common.AttendanceStatus
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.entities.StudentEntity
import com.miassolutions.rollcall.data.repository.impl.AttendanceRepoImpl
import com.miassolutions.rollcall.data.repository.impl.StudentRepoImpl
import com.miassolutions.rollcall.ui.model.AttendanceUIModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepo: AttendanceRepoImpl,
    private val studentRepo: StudentRepoImpl,
) : ViewModel() {

    private val _classId = MutableStateFlow("")
    val classId: StateFlow<String> = _classId.asStateFlow()

    private val _date = MutableStateFlow(Date().time)
    val date: StateFlow<Long> = _date.asStateFlow()

    private val _searchStudent = MutableStateFlow("")

    private val _studentList = MutableStateFlow<List<StudentEntity>>(emptyList())

    private val _attendanceUI = MutableStateFlow<List<AttendanceUIModel>>(emptyList())

    private val _filter = MutableStateFlow(AttendanceFilter.ALL)

    val filteredAttendanceUI: StateFlow<List<AttendanceUIModel>> = combine(
        _attendanceUI, _filter, _searchStudent
    ) { list, filter, query ->
        list.filter {
            when (filter) {
                AttendanceFilter.ALL -> true
                AttendanceFilter.PRESENT -> it.attendanceStatus == AttendanceStatus.PRESENT
                AttendanceFilter.ABSENT -> it.attendanceStatus == AttendanceStatus.ABSENT
            }
        }.filter {
            it.studentName.contains(query, ignoreCase = true) ||
                    query.toIntOrNull()?.let { roll -> it.rollNumber == roll } == true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCounts = _attendanceUI.map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val presentCount = _attendanceUI.map { list ->
        list.count { it.attendanceStatus == AttendanceStatus.PRESENT }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val absentCount = _attendanceUI.map { list ->
        list.count { it.attendanceStatus == AttendanceStatus.ABSENT }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setClassId(id: String) {
        _classId.value = id
        _date.value = Date().time // Automatically set today's date
        initializeData()
    }

    fun setDate(date: Long) {
        _date.value = date
    }

    fun updateSearchQuery(query: String) {
        _searchStudent.value = query
    }

    fun setFilter(filter: AttendanceFilter) {
        _filter.value = filter
    }

    fun updateAttendanceStatus(student: AttendanceUIModel, newStatus: AttendanceStatus) {
        _attendanceUI.update { list ->
            list.map {
                if (it.studentId == student.studentId) it.copy(attendanceStatus = newStatus)
                else it
            }
        }
    }

    fun saveAttendance(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val date = _date.value
            if (attendanceRepo.isAttendanceTaken(date)) {
                onResult(false)
            } else {
                val attendances = _attendanceUI.value.map {
                    AttendanceEntity(
                        classId = _classId.value,
                        studentId = it.studentId,
                        date = date,
                        attendanceStatus = it.attendanceStatus
                    )
                }
                attendanceRepo.insertAttendances(attendances)
                onResult(true)
            }
        }
    }

    fun updateAttendanceForDate(classId: String, date: Long, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            runCatching {
                val newList = _attendanceUI.value.map {
                    AttendanceEntity(
                        classId = classId,
                        studentId = it.studentId,
                        date = date,
                        attendanceStatus = it.attendanceStatus
                    )
                }
                attendanceRepo.replaceAttendanceForDate(classId, date, newList)
            }.onSuccess {
                onComplete(true)
            }.onFailure {
                it.printStackTrace()
                onComplete(false)
            }
        }
    }

    private fun initializeData() {
        _classId.filter { it.isNotEmpty() }
            .onEach { id ->
                studentRepo.getStudentListByClassId(id).collect { students ->
                    _studentList.value = students
                    updateAttendanceUI()
                }
            }
            .launchIn(viewModelScope)

        _date.filterNotNull()
            .onEach { updateAttendanceUI() }
            .launchIn(viewModelScope)
    }

    private suspend fun updateAttendanceUI() {
        if (_classId.value.isEmpty() || _studentList.value.isEmpty()) return

        val attendanceEntities = attendanceRepo.getClassAttendanceForDate(_date.value)

        _attendanceUI.value = _studentList.value.map { student ->
            val record = attendanceEntities.find { it.studentId == student.studentId }
            AttendanceUIModel(
                studentId = student.studentId,
                studentName = student.studentName,
                rollNumber = student.rollNumber,
                attendanceStatus = record?.attendanceStatus ?: AttendanceStatus.PRESENT
            )
        }
    }
}

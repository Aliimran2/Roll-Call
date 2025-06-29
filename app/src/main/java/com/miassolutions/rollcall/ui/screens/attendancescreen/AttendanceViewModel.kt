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

    private val _date = MutableStateFlow(0L) // Initialize with 0L or a default invalid date
    val date: StateFlow<Long> = _date.asStateFlow()

    private val _searchStudent = MutableStateFlow("")

    // Student list will now be derived from classId and will automatically update
    private val _studentList: StateFlow<List<StudentEntity>> = _classId
        .filter { it.isNotEmpty() } // Only proceed if classId is not empty
        .flatMapLatest { id ->
            studentRepo.getStudentListByClassId(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    private val _attendanceUI = MutableStateFlow<List<AttendanceUIModel>>(emptyList())

    private val _filter = MutableStateFlow(AttendanceFilter.ALL)

    // The core data flow for the UI, combining attendance data, filters, and search
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

    init {
        // Observe changes in _date and _studentList to trigger UI updates
        // This 'combine' ensures that updateAttendanceUI is called whenever
        // EITHER the date changes OR the student list (due to classId change) changes.
        combine(_date, _studentList) { dateMillis, students ->
            Pair(dateMillis, students)
        }.filter { (dateMillis, students) ->
            // Only proceed if classId is set, date is valid, and we have students
            _classId.value.isNotEmpty() && dateMillis != 0L && students.isNotEmpty()
        }.onEach { (dateMillis, students) ->
            updateAttendanceUI(dateMillis, students)
        }.launchIn(viewModelScope)
    }

    fun setClassId(id: String) {
        _classId.value = id
        // Do NOT automatically set date here. The date will be set by the Fragment
        // either to today's date for new attendance or to an existing date for viewing.
    }

    // This method now also triggers the updateAttendanceUI via the combined Flow in init{}
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
            val currentDate = _date.value
            val currentClassId = _classId.value

            if (currentClassId.isEmpty() || currentDate == 0L) {
                onResult(false) // Cannot save without classId or valid date
                return@launch
            }

            if (attendanceRepo.isAttendanceTaken(currentClassId, currentDate)) {
                onResult(false) // Attendance already taken for this class and date
            } else {
                val attendances = _attendanceUI.value.map {
                    AttendanceEntity(
                        classId = currentClassId,
                        studentId = it.studentId,
                        date = currentDate,
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

    // This function now takes date and students as parameters,
    // which come from the combined flow in the init block.
    private suspend fun updateAttendanceUI(dateMillis: Long, students: List<StudentEntity>) {
        if (dateMillis == 0L || students.isEmpty()) {
            _attendanceUI.value = emptyList() // Clear UI if no valid date or students
            return
        }

        val attendanceEntities = attendanceRepo.getClassAttendanceForDate(dateMillis)

        // If attendanceEntities is empty (no record for this date),
        // we create a list of students with default PRESENT status.
        // Otherwise, we match students with existing attendance records.
        _attendanceUI.value = students.map { student ->
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
package com.miassolutions.rollcall.ui.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.ui.model.AttendanceUIModel
import com.miassolutions.rollcall.common.AttendanceFilter
import com.miassolutions.rollcall.common.AttendanceStatus
import com.miassolutions.rollcall.data.repository.AttendanceRepository
import com.miassolutions.rollcall.data.repository.impl.AttendanceRepoImpl
import com.miassolutions.rollcall.data.repository.impl.ClassRepoImpl
import com.miassolutions.rollcall.data.repository.impl.StudentRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepo: AttendanceRepoImpl,
    private val studentRepo: StudentRepoImpl,
    private val classRepo: ClassRepoImpl,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState = _uiState.asStateFlow()

    val filteredAttendance: StateFlow<List<AttendanceUiState.StudentAttendance>> =
        combine(
            _uiState,
            _uiState.map { it.allStudents },
            _uiState.map { it.filter },
            _uiState.map { it.searchQuery }
        ) { state, allStudents, filter, query ->
            allStudents.filter {
                when (filter) {
                    AttendanceFilter.ALL -> true
                    AttendanceFilter.PRESENT -> it.status == AttendanceStatus.PRESENT
                    AttendanceFilter.ABSENT -> it.status == AttendanceStatus.ABSENT
                }
            }
                .filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.rollNumber.contains(query, ignoreCase = true)
                }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onEvent(event: AttendanceUiEvent) {
        when (event) {
            is AttendanceUiEvent.SetDate -> setDate(event.date)
            is AttendanceUiEvent.UpdateFilter -> updateFilter(event.filter)
            is AttendanceUiEvent.UpdateStatus -> updateStatus(event.studentId, event.newStatus)
            is AttendanceUiEvent.UpdateSearchQuery -> updateSearchQuery(event.query)
            AttendanceUiEvent.SaveAttendance -> saveAttendance()
        }
    }

    private fun updateStatus(studentId: String, newStatus: AttendanceStatus) {
        _uiState.update { currentState ->
            val updatedStudents = currentState.allStudents.map { student ->
                if (student.studentId == studentId) student.copy(status = newStatus) else student
            }
            currentState.copy(
                allStudents = updatedStudents,
                counts = calculateCounts(updatedStudents)
            )

        }
    }

    private fun saveAttendance() {
        val date = _uiState.value.selectedDate ?: return
        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            try {
                val attendanceRecords = _uiState.value.allStudents.map {
                    AttendanceEntity(
                        studentId = it.studentId,
                        date = date,
                        attendanceStatus = it.status,
                        classId = it.classId
                    )
                }

                if (attendanceRepo.isAttendanceTaken(date)) {
                    attendanceRepo.insertAttendances(attendanceRecords)
                } else {
                    attendanceRepo.insertAttendances(attendanceRecords)
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Failed to save attendance : ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    private fun updateFilter(filter: AttendanceFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    private fun calculateCounts(students: List<AttendanceUiState.StudentAttendance>): AttendanceUiState.AttendanceCounts {
        return AttendanceUiState.AttendanceCounts(
            total = students.size,
            present = students.count { it.status == AttendanceStatus.PRESENT },
            absent = students.count { it.status == AttendanceStatus.ABSENT }
        )
    }

    private fun setDate(date: Long) {
        _uiState.update { it.copy(selectedDate = date) }
        loadAttendance(date)
    }

    private fun loadAttendance(date: Long) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val students = studentRepo.getStudentsByClassId("abc").first()
                val attendance = attendanceRepo.getClassAttendanceForDate(date)

                val studentAttendances = students.map { student ->
                    val status =
                        attendance.find { it.studentId == student.studentId }?.attendanceStatus
                            ?: AttendanceStatus.PRESENT
                    AttendanceUiState.StudentAttendance(
                        studentId = student.studentId,
                        name = student.studentName,
                        rollNumber = student.rollNumber.toString(),
                        status = status,
                        classId = student.classId
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allStudents = studentAttendances,
                        counts = calculateCounts(studentAttendances)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load attendance : ${e.localizedMessage}"
                    )
                }
            }
        }
    }


//    // --- Public State ---
//
//    private val studentList = repository.allStudentsFlow
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
//
//    private val _selectedDate = MutableStateFlow<Long?>(null)
//    val selectedDate: StateFlow<Long?> = _selectedDate
//
//    private val _attendanceUI = MutableStateFlow<List<AttendanceUIModel>>(emptyList())
//    val attendanceUI: StateFlow<List<AttendanceUIModel>> = _attendanceUI
//
//    private val _filter = MutableStateFlow(AttendanceFilter.ALL)
//    val filter: StateFlow<AttendanceFilter> = _filter.asStateFlow()
//
//    fun setFilter(filter: AttendanceFilter) {
//        _filter.value = filter
//    }
//
//    private val _searchStudent = MutableStateFlow("")
//    private val searchStudent = _searchStudent.asStateFlow()
//
//    fun updateSearchQuery(query: String) {
//        _searchStudent.value = query
//    }
//
//
//    val filteredAttendanceUI: StateFlow<List<AttendanceUIModel>> = combine(
//        _attendanceUI, _filter, _searchStudent
//    ) { list, filter, searchStudent ->
//        list
//            .filter {
//                //apply the filter first
//                when (filter) {
//                    AttendanceFilter.ALL -> true
//                    AttendanceFilter.PRESENT -> it.attendanceStatus == AttendanceStatus.PRESENT
//                    AttendanceFilter.ABSENT -> it.attendanceStatus == AttendanceStatus.ABSENT
//                }
//            }
//            .filter {
//                it.studentName.contains(searchStudent, ignoreCase = true)
//                        || it.rollNumber.toString() == searchStudent
//            }
//
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
//
//
//    // --- Init block to collect studentList + selectedDate ---
//    init {
//        viewModelScope.launch {
//            combine(studentList, selectedDate.filterNotNull()) { students, date ->
//                val attendanceEntities = repository.getAttendanceForDate(date)
//
//                students.map { student ->
//                    val match = attendanceEntities.find { it.studentId == student.studentId }
//                    AttendanceUIModel(
//                        studentId = student.studentId,
//                        studentName = student.studentName,
//                        rollNumber = student.rollNumber,
//                        attendanceStatus = match?.attendanceStatus ?: AttendanceStatus.PRESENT
//                    )
//                }
//            }.collectLatest { attendanceModels ->
//                _attendanceUI.value = attendanceModels
//            }
//        }
//    }
//
//    // --- Actions ---
//
//    fun setDate(date: Long) {
//        _selectedDate.value = date
//    }
//
//    fun updateAttendanceStatus(student: AttendanceUIModel, newStatus: AttendanceStatus) {
//        _attendanceUI.value = _attendanceUI.value.map {
//            if (it.studentId == student.studentId) it.copy(attendanceStatus = newStatus) else it
//        }
//    }
//
//    fun saveAttendance(onResult: (Boolean) -> Unit) {
//        viewModelScope.launch {
//            val date = _selectedDate.value ?: return@launch
//            val alreadyExists = repository.isAttendanceTaken(date)
//
//            if (alreadyExists) {
//                onResult(false)
//            } else {
//                val entityList = _attendanceUI.value.map {
//                    AttendanceEntity(
//                        studentId = it.studentId,
//                        date = date,
//                        attendanceStatus = it.attendanceStatus
//                    )
//                }
//                repository.insertAttendances(entityList)
//                onResult(true)
//            }
//        }
//    }
//
//    fun updateAttendanceForDate(date: Long, onComplete: (Boolean) -> Unit) {
//        viewModelScope.launch {
//            try {
//                repository.replaceAttendanceForDate(date, _attendanceUI.value.map {
//                    AttendanceEntity(
//                        studentId = it.studentId,
//                        date = date,
//                        attendanceStatus = it.attendanceStatus
//                    )
//                })
//                onComplete(true)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                onComplete(false)
//            }
//        }
//    }
//
//    //counts
//
//    val totalCount = attendanceUI.map { it.size }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
//
//    val presentCount = attendanceUI.map { list ->
//        list.count { it.attendanceStatus == AttendanceStatus.PRESENT }
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
//
//    val absentCount = attendanceUI.map { list ->
//        list.count { it.attendanceStatus == AttendanceStatus.ABSENT }
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}

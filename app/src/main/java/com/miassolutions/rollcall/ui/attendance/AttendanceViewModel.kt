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
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceUiState())
    val uiState = _uiState.asStateFlow()

    fun setClassId(classId: String) {
        _uiState.update { it.copy(classId = classId) }
    }

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
        val classId = _uiState.value.classId
        if (classId.isEmpty()) return

        val date = _uiState.value.selectedDate ?: return
        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            try {
                val attendanceRecords = _uiState.value.allStudents.map {
                    AttendanceEntity(
                        studentId = it.studentId,
                        date = date,
                        attendanceStatus = it.status,
                        classId = classId
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

    fun setDate(date: Long) {
        _uiState.update { it.copy(selectedDate = date) }
        loadAttendance(date)
    }

    private fun loadAttendance(date: Long) {
        val classId = _uiState.value.classId
        if (classId.isEmpty()) return

        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                val students = studentRepo.getStudentListByClassId(classId).first()
                val attendance = attendanceRepo.getClassAttendanceForDate(date)

                val studentAttendances = students.map { student ->
                    val status =
                        attendance.find { it.studentId == student.studentId }?.attendanceStatus
                            ?: AttendanceStatus.PRESENT
                    AttendanceUiState.StudentAttendance(
                        studentId = student.studentId,
                        name = student.studentName,
                        rollNumber = student.rollNumber.toString(),
                        status = status
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

}

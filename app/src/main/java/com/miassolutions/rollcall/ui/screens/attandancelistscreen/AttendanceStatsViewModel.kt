package com.miassolutions.rollcall.ui.screens.attandancelistscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.common.AttendanceStatus
import com.miassolutions.rollcall.data.repository.impl.AttendanceRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceStatsViewModel @Inject constructor(private val repository: AttendanceRepoImpl) :
    ViewModel() {


    private val _uiState = MutableStateFlow(AttendanceListUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AttendanceStatsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private var classId: String = ""

    fun setClassId(id: String) {
        classId = id
    }

    init {
        loadAttendanceStats()
    }


    private fun loadAttendanceStats() {
        viewModelScope.launch {
            repository.getClassAttendanceGroupedByDate(classId)
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.emit(AttendanceStatsUiEvent.ShowSnackbar("Error : ${e.localizedMessage}"))
                }
                .collectLatest { groupedMap ->
                    val statsList = groupedMap.map { (dateInMillis, attendanceList) ->
                        val presentCount =
                            attendanceList.count { it.attendanceStatus == AttendanceStatus.PRESENT }
                        val totalCount = attendanceList.size
                        val percentage = if (totalCount == 0) 0 else presentCount * 100 / totalCount

                        AttendanceStatsItem(
                            date = dateInMillis,
                            presentCount = presentCount,
                            totalCount = totalCount,
                            percentage = percentage
                        )

                    }.sortedByDescending { it.date }

                    _uiState.update { it.copy(isLoading = false, attendanceStats = statsList) }
                }


        }
    }

    fun onEditClick(attendanceId: String) {
        viewModelScope.launch {
            _uiEvent.emit(AttendanceStatsUiEvent.NavToAddEditAttendance(attendanceId))
        }
    }

    fun onDeleteClick(attendanceId: String) {
        viewModelScope.launch {
            _uiEvent.emit(AttendanceStatsUiEvent.ShowDeleteConfirmation(attendanceId))
        }
    }

    fun onReportClick(attendanceId: String) {
        viewModelScope.launch {
            _uiEvent.emit(AttendanceStatsUiEvent.NavToReportAttendance(attendanceId))
        }
    }

}
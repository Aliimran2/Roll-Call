package com.miassolutions.rollcall.ui.screens.attandancelistscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.common.AttendanceStatus
import com.miassolutions.rollcall.data.repository.impl.AttendanceRepoImpl
import com.miassolutions.rollcall.extenstions.toFormattedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceStatsViewModel @Inject constructor(
    private val repository: AttendanceRepoImpl,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceListUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<AttendanceStatsUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var classId: String = ""

    fun setClassId(id: String) {
        classId = id
        loadAttendanceStats() // Start loading once classId is set
    }

    private fun loadAttendanceStats() {
        viewModelScope.launch {
            repository.getClassAttendanceGroupedByDate(classId)
                .onStart {
                    _uiState.update { it.copy(isLoading = true) }
                }
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.trySend(
                        AttendanceStatsUiEvent.ShowSnackbar("Error: ${e.localizedMessage}")
                    )
                }
                .collectLatest { groupedMap ->
                    val statsList = groupedMap.map { (date, attendanceList) ->
                        val presentCount =
                            attendanceList.count { it.attendanceStatus == AttendanceStatus.PRESENT }
                        val totalCount = attendanceList.size
                        val percentage = if (totalCount == 0) 0 else presentCount * 100 / totalCount

                        AttendanceStatsItem(
                            date = date,
                            presentCount = presentCount,
                            totalCount = totalCount,
                            percentage = percentage
                        )
                    }.sortedByDescending { it.date }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            attendanceStats = statsList
                        )
                    }
                }
        }
    }

    fun onEditClick(date: Long) {
        emitEvent(AttendanceStatsUiEvent.NavToAddEditAttendance(date))
    }

    fun onDeleteClick(date: Long) {
        viewModelScope.launch {
            try {
                repository.deleteAttendancesForClassAndDate(classId, date)
                loadAttendanceStats()
                emitEvent(AttendanceStatsUiEvent.ShowSnackbar("Deleted record for date ${date.toFormattedDate()}"))

            } catch (e:Exception){
                emitEvent(AttendanceStatsUiEvent.ShowSnackbar("Failed : ${e.localizedMessage}"))
            }
        }

    }

    fun onReportClick(date: Long) {
        emitEvent(AttendanceStatsUiEvent.NavToReportAttendance(date))
    }

    private fun emitEvent(event: AttendanceStatsUiEvent) {
        viewModelScope.launch {
            _uiEvent.trySend(event)
        }
    }

}

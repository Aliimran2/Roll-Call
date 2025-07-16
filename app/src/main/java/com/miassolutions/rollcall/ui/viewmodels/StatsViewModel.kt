package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.common.AttendanceStatus
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.ui.model.StatsUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate = _selectedDate.asStateFlow()

    fun setDate(date: LocalDate?) {
        _selectedDate.value = date
    }


    val attendanceSummary =
        repository.getAttendanceGroupedByDate()
            .map { groupedMap ->
                groupedMap.map { (date, attendanceList) ->
                    val presentCount =
                        attendanceList.count { it.attendanceStatus == AttendanceStatus.PRESENT }
                    val totalCount = attendanceList.size
                    StatsUiModel(
                        date,
                        presentCount,
                        totalCount
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val filteredSummary = combine(_selectedDate, attendanceSummary) { selectedDate, summaryList ->
        if (selectedDate == null) {
            summaryList
        } else {
            summaryList.filter { it.date == selectedDate }
        }
    }

    fun deleteAttendance(date: LocalDate) {
        viewModelScope.launch {
            repository.deleteAttendanceForDate(date)
        }
    }

    fun updateStudentsAttendanceForDate(updatedList: List<AttendanceEntity>) {
        viewModelScope.launch {
            repository.updateAttendances(updatedList)
        }
    }


}
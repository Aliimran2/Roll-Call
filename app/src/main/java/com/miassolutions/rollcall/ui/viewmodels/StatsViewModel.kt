package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.ui.model.StatsUiModel
import com.miassolutions.rollcall.common.AttendanceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {


    val attendanceSummary =
        repository.getAttendanceGroupedByDate()
            .map { groupedMap ->
                groupedMap.map { (dateMillis, attendanceList) ->
                    val presentCount =
                        attendanceList.count { it.attendanceStatus == AttendanceStatus.PRESENT }
                    val totalCount = attendanceList.size
                    StatsUiModel(
                        dateMillis,
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

    fun deleteAttendance(date: Long) {
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
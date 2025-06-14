package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.ui.model.StatsUiModel
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.utils.AttendanceStatus
import com.miassolutions.rollcall.utils.toFormattedDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

//    fun loadAttendanceSummary(): LiveData<List<StatsUiModel>> = liveData {
//        val attendanceGroupedByDate = repository.getAttendanceGroupedByDate()
//        val uiList = attendanceGroupedByDate.map { (date, list) ->
//            StatsUiModel(
//                date = date,
//                presentCount = list.count { it.attendanceStatus == AttendanceStatus.PRESENT },
//                totalCount = list.size
//            )
//        }
//
//        emit(uiList)
//    }

    val attendanceSummary =
        repository.getAttendanceGroupedByDate()
            .map { groupedMap ->
                groupedMap.map { (dateMillis, attendanceList) ->
                    val presentCount =
                        attendanceList.count() { it.attendanceStatus == AttendanceStatus.PRESENT }
                    val totalCount = attendanceList.size
                    StatsUiModel(
                        dateMillis.toFormattedDate(),
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


}
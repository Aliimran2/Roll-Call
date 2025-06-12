package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.miassolutions.rollcall.ui.model.StatsUiModel
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.utils.AttendanceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    fun loadAttendanceSummary(): LiveData<List<StatsUiModel>> = liveData {
        val attendanceGroupedByDate = repository.getAttendanceGroupedByDate()
        val uiList = attendanceGroupedByDate.map { (date, list) ->
            StatsUiModel(
                date = date,
                presentCount = list.count { it.attendanceStatus == AttendanceStatus.PRESENT },
                totalCount = list.size
            )
        }

        emit(uiList)
    }

}
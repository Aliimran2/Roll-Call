package com.miassolutions.rollcall.ui.model

import android.icu.util.LocaleData
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import java.time.LocalDate

data class AttendanceUiState(
    val dateRange : Pair<LocaleData, LocalDate>,
    val attendanceList: List<AttendanceEntity> = emptyList(),
    val isLoading : Boolean = false
)

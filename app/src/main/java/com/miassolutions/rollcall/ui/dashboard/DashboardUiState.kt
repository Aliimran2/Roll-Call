package com.miassolutions.rollcall.ui.dashboard

data class DashboardUiState(
    val isLoading: Boolean = false,
    val isAttendanceTaken: Boolean = false,
    val counts : AttendanceCounts? = null,
    val userName : String? = null,
    val instituteName : String? = null,
    val profileImgUri : String? = null,
    val errorMessage : String? = null

    )


data class AttendanceCounts(
    val total: String = "",
    val present: String = "",
    val absent: String = ""
)
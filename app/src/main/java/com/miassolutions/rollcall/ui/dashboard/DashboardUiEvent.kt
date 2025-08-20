package com.miassolutions.rollcall.ui.dashboard

sealed class DashboardUiEvent {
    data object NavigateToAttendance : DashboardUiEvent()
    data object NavigateToStudents : DashboardUiEvent()
    data object NavigateToUserProfile : DashboardUiEvent()
    data object NavigateToSettings : DashboardUiEvent()
    data object ShowReportMessage : DashboardUiEvent()
}
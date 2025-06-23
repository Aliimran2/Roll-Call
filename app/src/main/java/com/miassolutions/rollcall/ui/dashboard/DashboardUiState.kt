package com.miassolutions.rollcall.ui.dashboard

import android.net.Uri

data class DashboardUiState(
    val userProfileImageUri : String? = null,
    val userName : String = "User",
    val instituteName : String = "Your Institute",
    val isLoading : Boolean = false, //future use
    val errorMessage : String? = null
)


sealed class DashBoardUiEvent {
    data object NavigateToAttendance : DashBoardUiEvent()
    data object NavigateToSettings : DashBoardUiEvent()
    data object NavigationToProfile : DashBoardUiEvent()
    data object NavigationToClasses : DashBoardUiEvent()
}
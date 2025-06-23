package com.miassolutions.rollcall.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.datastore.UserPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val prefs: UserPrefsManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<DashBoardUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    init {
        collectDashboardData()
    }


    private fun collectDashboardData() {
        combine(
            prefs.userName,
            prefs.instituteName,
            prefs.userProfileImage
        ) { userName, instName, userImage ->
            _uiState.update {
                it.copy(
                    userName = userName?.uppercase() ?: "User Name",
                    userProfileImageUri = userImage,
                    instituteName = instName?.uppercase() ?: "Institute Name"
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAttendanceCardClicked() {
        viewModelScope.launch {
            _uiEvent.send(DashBoardUiEvent.NavigateToAttendance)
        }
    }

    fun onSettingsCardClicked() {
        viewModelScope.launch {
            _uiEvent.send(DashBoardUiEvent.NavigateToSettings)
        }
    }

    fun onClassesCardClicked() {
        viewModelScope.launch {
            _uiEvent.send(DashBoardUiEvent.NavigationToClasses)
        }
    }

    fun onProfileCardClicked() {
        viewModelScope.launch {
            _uiEvent.send(DashBoardUiEvent.NavigationToProfile)
        }
    }

}
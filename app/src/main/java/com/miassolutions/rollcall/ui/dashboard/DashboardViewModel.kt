package com.miassolutions.rollcall.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.datastore.UserPrefsManager
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.ui.settings.SettingsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel @Inject constructor(
    private val repository: Repository,
    private val prefs: UserPrefsManager
) : ViewModel() {


    private val selectedDate = MutableStateFlow(LocalDate.now())

    private val _uiEvent = MutableSharedFlow<DashboardUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val isAttendanceTaken: Flow<Boolean> = selectedDate.flatMapLatest { date ->
        repository.isAttendanceTaken(date)
    }

    private val attendanceCounts: Flow<AttendanceCounts> = selectedDate.flatMapLatest { date ->
        combine(
            repository.getPresentCount(date),
            repository.getTotalCount(),
            isAttendanceTaken
        ) { present, total, taken ->
            if (taken) {
                AttendanceCounts(
                    total = total.toString(),
                    present = present.toString(),
                    absent = (total - present).toString()
                )
            } else {
                AttendanceCounts(
                    total = total.toString(),
                    present = "0",
                    absent = "0"
                )
            }
        }
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        isAttendanceTaken,
        attendanceCounts,
        prefs.userName,
        prefs.instituteName,
        prefs.userProfileImage
    ) { taken, counts, name, institute, image ->
        DashboardUiState(

            isAttendanceTaken = taken,
            counts = counts,
            userName = name,
            instituteName = institute,
            profileImgUri = image,

            )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    fun onAttendanceClicked() = sendEvent(DashboardUiEvent.NavigateToAttendance)
    fun onReportClicked() = sendEvent(DashboardUiEvent.ShowReportMessage)
    fun onUserClicked() = sendEvent(DashboardUiEvent.NavigateToUserProfile)
    fun onStudentsClicked() = sendEvent(DashboardUiEvent.NavigateToStudents)
    fun onSettingsClicked() = sendEvent(DashboardUiEvent.NavigateToSettings)

    private fun sendEvent(event: DashboardUiEvent) {
        viewModelScope.launch { _uiEvent.emit(event) }
    }


//    val attendanceCounts: StateFlow<Counts> = selectedDate
//        .flatMapLatest { date ->
//            combine(
//                repository.getPresentCount(date),
//                repository.getTotalCount(),
//                isAttendanceTaken
//            ) { present, total, taken ->
//                if (taken) {
//                    Counts(
//                        total = total.toString(),
//                        present = present.toString(),
//                        absent = (total - present).toString()
//                    )
//                } else {
//                    Counts(
//                        total = total.toString(),
//                        present = "0",
//                        absent = "0"
//                    )
//                }
//
//
//            }
//        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Counts())


    data class Counts(
        val total: String = "",
        val present: String = "",
        val absent: String = "",
    )


}
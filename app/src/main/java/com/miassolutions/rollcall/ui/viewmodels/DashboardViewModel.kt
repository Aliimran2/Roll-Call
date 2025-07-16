package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel @Inject constructor(private val repository: Repository) : ViewModel() {


    private val selectedDate = MutableStateFlow(LocalDate.now())



    val isAttendanceTaken: StateFlow<Boolean> = selectedDate
        .flatMapLatest { date ->
            repository.isAttendanceTaken(date)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    val attendanceCounts: StateFlow<Counts> = selectedDate
        .flatMapLatest { date ->
            combine(
                repository.getPresentCount(date),
                repository.getTotalCount(),
                isAttendanceTaken
            ) { present, total, taken ->
                if (taken) {
                    Counts(
                        total = total.toString(),
                        present = present.toString(),
                        absent = (total - present).toString()
                    )
                } else {
                    Counts(
                        total = total.toString(),
                        present = "0",
                        absent = "0"
                    )
                }


            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Counts())



    data class Counts(
        val total: String = "",
        val present: String = "",
        val absent: String = "",
    )


}
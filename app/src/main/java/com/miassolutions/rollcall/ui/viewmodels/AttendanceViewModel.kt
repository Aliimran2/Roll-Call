package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.Attendance
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.utils.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AttendanceViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val studentList = repository.allStudents.asLiveData()

    fun saveAttendance(list: List<Attendance>) {
        viewModelScope.launch {
            repository.insertAttendances(list)
        }
    }

    private val _selectedDate = MutableStateFlow(getCurrentDate())

    // UI observable counts as StateFlow<Int>

    val totalMarkedCount: StateFlow<Int> = _selectedDate.flatMapLatest { date ->
        repository.getTotalMarkedStudentsCount(date)
            .catch { emit(0) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val presentCount: StateFlow<Int> = _selectedDate.flatMapLatest { date ->
        repository.getPresentStudentsCount(date)
            .catch { emit(0) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    val absentCount: StateFlow<Int> = _selectedDate.flatMapLatest { date ->
        repository.getAbsentStudentsCount(date)
            .catch { emit(0) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // To update date (if needed)
    fun setDate(date: String) {
        _selectedDate.value = date
    }

}
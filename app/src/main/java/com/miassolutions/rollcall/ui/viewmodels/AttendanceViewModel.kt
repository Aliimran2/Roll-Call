package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.Attendance
import com.miassolutions.rollcall.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    val studentList = repository.allStudents.asLiveData()

    fun saveAttendance(list: List<Attendance>) {
        viewModelScope.launch {
            repository.insertAttendances(list)
        }
    }
}
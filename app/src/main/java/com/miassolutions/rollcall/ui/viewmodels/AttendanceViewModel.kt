package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.entities.AttendanceEntity
import com.miassolutions.rollcall.data.repository.Repository
import com.miassolutions.rollcall.ui.model.AttendanceUIModel
import com.miassolutions.rollcall.utils.AttendanceStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    // Exposes a LiveData stream of all students from the repository.
    // LiveData is lifecycle-aware, making it suitable for UI observation.
    val studentList = repository.allStudents.asLiveData()



    // A MutableStateFlow to hold the currently selected date for attendance.
    // It's initialized with the current date using a utility function.
    private val _selectedDate = MutableStateFlow("")

    // A MutableStateFlow to hold the list of AttendanceUIModel objects.
    // This list represents the attendance status of students for the selected date.
    private val _attendanceUI = MutableStateFlow<List<AttendanceUIModel>>(emptyList())
    val attendanceUI: StateFlow<List<AttendanceUIModel>> = _attendanceUI

    // Counts
    // measure the size of the list
    // These StateFlows provide real-time counts based on the attendanceUI list.
    // They use the .map operator to transform the list into a count and
    // .stateIn to convert the Flow into a StateFlow, ensuring it's shared
    // across observers and remains active as long as there are collectors.
    val totalCount: StateFlow<Int> = attendanceUI.map { attendanceList: List<AttendanceUIModel> ->
        attendanceList.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val presentCount: StateFlow<Int> = attendanceUI.map { attendanceList: List<AttendanceUIModel> ->
        attendanceList.count { student -> student.attendanceStatus == AttendanceStatus.PRESENT }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val absentCount: StateFlow<Int> = attendanceUI.map { attendanceList: List<AttendanceUIModel> ->
        attendanceList.count { student -> student.attendanceStatus == AttendanceStatus.ABSENT }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)



    // --- Actions ---

    /**
     * Saves the current attendance data to the repository.
     * It first checks if attendance for the selected date has already been taken.
     * If not, it converts the UI models to entity models and inserts them.
     * @param onResult A callback function to indicate whether the save operation was successful (true) or if attendance already existed (false).
     */
    fun saveAttendance(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val date = _selectedDate.value
            val alreadyExists = repository.isAttendanceTaken(date)

            if (alreadyExists) {
                onResult(false)
            } else {

                val attendanceEntityList = _attendanceUI.value.map {
                    AttendanceEntity(
                        studentId = it.studentId,
                        date = date,
                        attendanceStatus = it.attendanceStatus
                    )
                }
                repository.insertAttendances(attendanceEntityList)
                onResult(true)
            }


        }
    }

    /**
     * Sets the initial list of students for attendance tracking.
     * This is typically called when the UI is first initialized with student data.
     * @param students The list of AttendanceUIModel objects representing the students.
     */
    fun setInitialAttendanceList(students: List<AttendanceUIModel>) {
        _attendanceUI.value = students
    }


    /**
     * Updates the attendance status of a specific student in the attendance list.
     * This is called when the toggle (e.g., switch or checkbox) for a student's attendance changes.
     * It creates a new list with the updated student's status, triggering UI updates.
     * @param student The AttendanceUIModel of the student whose status is being updated.
     * @param newStatus The new AttendanceStatus (PRESENT or ABSENT).
     */
    fun updateAttendanceStatus(student: AttendanceUIModel, newStatus: AttendanceStatus) {
        _attendanceUI.value = _attendanceUI.value.map {
            if (it.studentId == student.studentId) it.copy(attendanceStatus = newStatus) else it
        }
    }

    /**
     * Sets the selected date for attendance.
     * @param date The date string in the format used by the application.
     */
    fun setDate(date: String) {
        _selectedDate.value = date
    }

}
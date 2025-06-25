package com.miassolutions.rollcall.ui.studentlistscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import com.miassolutions.rollcall.data.repository.impl.StudentRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StudentLitViewModel @Inject constructor(private val studentRepo: StudentRepoImpl) :
    ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val mClassId = MutableStateFlow("")

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun updateClassId(classId: String) {
        mClassId.value = classId

    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val filteredStudents = searchQuery
        .debounce(200L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                studentRepo.getStudentsByClassId(classId = mClassId.value)
            } else {
                studentRepo.searchStudents(query)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


}



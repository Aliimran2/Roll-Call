package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {


    private val _deleteAllMessage = MutableStateFlow<String>("")
    val deleteAllMessage = _deleteAllMessage.asSharedFlow()

    fun deleteAll() {
        viewModelScope.launch {
            repository.allStudents.collectLatest { students ->
                repository.deleteAll(students)
                _deleteAllMessage.value = "All students deleted"
            }
        }
    }


}
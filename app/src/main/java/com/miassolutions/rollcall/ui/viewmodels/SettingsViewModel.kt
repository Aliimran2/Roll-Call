package com.miassolutions.rollcall.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {


    private val _messageEvent = MutableSharedFlow<String>()
    val messageEvent: SharedFlow<String> = _messageEvent

    fun deleteAll() {
        viewModelScope.launch {
            try {
                repository.clearAllStudents()
                // Emit a success message after deletion
                _messageEvent.emit("All students deleted successfully!")
            } catch (e: Exception) {
                // Emit an error message if something goes wrong
                _messageEvent.emit("Error deleting students: ${e.localizedMessage}")
                // Log the exception for debugging
                e.printStackTrace()
            }
        }
    }


}
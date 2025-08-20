package com.miassolutions.rollcall.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.datastore.UserPrefsManager
import com.miassolutions.rollcall.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: Repository,
    private val prefs: UserPrefsManager
) : ViewModel() {


    private val _messageEvent = MutableSharedFlow<String>()
    val messageEvent: SharedFlow<String> = _messageEvent

    val userName = prefs.userName
    val instituteName = prefs.instituteName
    val userProfileImage = prefs.userProfileImage


    fun saveImageUriStr(imagePath : String){
        viewModelScope.launch {
            prefs.saveUserImage(imagePath)
        }
    }




    private fun saveUserName(userName: String) {
        viewModelScope.launch {
            prefs.saveUserName(userName)
        }
    }

    private fun saveInstituteName(instName: String) {
        viewModelScope.launch {
            prefs.saveInstituteName(instName)

        }
    }

    fun saveUserProfile(userName: String, instituteName: String) {

        saveUserName(userName)
        saveInstituteName(instituteName)
    }



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
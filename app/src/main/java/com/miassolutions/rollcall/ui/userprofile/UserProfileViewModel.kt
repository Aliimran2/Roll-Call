package com.miassolutions.rollcall.ui.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.datastore.UserPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val prefs: UserPrefsManager
) : ViewModel() {


    private val _messageEvent = MutableSharedFlow<String>()
    val messageEvent: SharedFlow<String> = _messageEvent


    val userName = prefs.userName.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val instituteName = prefs.instituteName.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val userProfileImage = prefs.userProfileImage.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveImageUriStr(imagePath : String){
        viewModelScope.launch {
            prefs.saveUserImage(imagePath)
        }
    }

    private fun saveUserName(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.saveUserName(userName)
        }
    }

    private fun saveInstituteName(instName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.saveInstituteName(instName)

        }
    }

    fun saveUserProfile(userName: String, instituteName: String) {
        saveUserName(userName)
        saveInstituteName(instituteName)
    }
}
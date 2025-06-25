package com.miassolutions.rollcall.ui.screens.userprofilescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miassolutions.rollcall.data.datastore.UserPrefsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val prefs: UserPrefsManager,
) : ViewModel() {

    private val _uiEvent = Channel<UserProfileUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        combine(
            prefs.userProfileImage,
            prefs.userName,
            prefs.instituteName
        ) { imagePath, userName, instituteName ->
            _uiState.update {
                it.copy(
                    userName = userName ?: "",
                    instituteName = instituteName ?: "",
                    userProfileImage = imagePath,
                    isLoading = false
                )
            }
        }.launchIn(viewModelScope)
    }

    fun saveImageUrlStr(imagePath: String) {
        viewModelScope.launch {
            prefs.saveUserImage(imagePath)
        }
    }

    fun validateAndSaveProfile(userName: String, instituteName: String) {
        when {
            userName.isBlank() -> {
                viewModelScope.launch {
                    _uiEvent.send(
                        UserProfileUiEvent.ShowValidationError(
                            Field.USER_NAME,
                            "Enter name"
                        )
                    )
                }
            }

            instituteName.isBlank() -> {
                viewModelScope.launch {
                    _uiEvent.send(
                        UserProfileUiEvent.ShowValidationError(
                            Field.INSTITUTE_NAME,
                            "Enter institute name"
                        )
                    )
                }
            }

            else -> saveUserProfile(userName, instituteName)
        }
    }

    private fun saveUserProfile(userName: String, instituteName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                prefs.saveUserName(userName)
                prefs.saveInstituteName(instituteName)
                _uiEvent.send(UserProfileUiEvent.NavigateUp)

            } catch (e: Exception) {
                _uiEvent.send(UserProfileUiEvent.ShowToast("Error saving profile : ${e.localizedMessage}"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }

        }
    }
}
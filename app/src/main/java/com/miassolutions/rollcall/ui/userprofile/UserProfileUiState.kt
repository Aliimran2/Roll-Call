package com.miassolutions.rollcall.ui.userprofile



data class UserProfileUiState (
    val userProfileImage : String? = null,
    val userName : String = "",
    val instituteName : String = "",
    val isLoading : Boolean = false,
)


sealed class UserProfileUiEvent {
    data object NavigateUp : UserProfileUiEvent()
    data class ShowToast(val message : String) : UserProfileUiEvent()
    data class ShowValidationError(val field : Field, val message: String) : UserProfileUiEvent()
}

enum class Field {USER_NAME, INSTITUTE_NAME}
package com.miassolutions.rollcall.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.miassolutions.rollcall.common.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStore by preferencesDataStore(Constants.USER_PREFS_NAME)

@Singleton
class UserPrefsManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val MIN_DATE_KEY = longPreferencesKey("min_date_key")
        private val USER_NAME_KEY = stringPreferencesKey("user_name_key")
        private val INSTITUTE_NAME_KEY = stringPreferencesKey("institute_name_key")
        private val USER_PROFILE_IMAGE = stringPreferencesKey("user_profile_image")
    }

    suspend fun saveUserImage(imageUri : String){
        dataStore.edit { prefs ->
            prefs[USER_PROFILE_IMAGE] = imageUri
        }
    }

    val userProfileImage = dataStore.data.map {prefs ->
        prefs[USER_PROFILE_IMAGE]
    }

    //Save minDate
    suspend fun saveMinDate(minDate: Long) {
        dataStore.edit { prefs ->
            prefs[MIN_DATE_KEY] = minDate
        }
    }

    suspend fun saveInstituteName(instituteName: String) {
        dataStore.edit { prefs ->
            prefs[INSTITUTE_NAME_KEY] = instituteName
        }
    }

    //Read minDate
    val minDate: Flow<Long?> = dataStore.data.map { prefs ->
        prefs[MIN_DATE_KEY]
    }

    suspend fun saveUserName(userName: String) {
        dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = userName
        }
    }

    val userName: Flow<String?> = dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY]
    }

    val instituteName: Flow<String?> = dataStore.data.map { prefs ->
        prefs[INSTITUTE_NAME_KEY]
    }




}
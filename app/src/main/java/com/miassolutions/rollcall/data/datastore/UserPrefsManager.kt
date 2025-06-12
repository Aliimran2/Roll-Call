package com.miassolutions.rollcall.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.miassolutions.rollcall.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStore by preferencesDataStore(Constants.USER_PREFS_NAME)

@Singleton
class UserPrefsManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val dataStore = context.dataStore

    private val minDateKey = longPreferencesKey(Constants.MIN_DATE_KEY)
    private val userNameKey = stringPreferencesKey(Constants.USER_NAME_KEY)

    //Save minDate
    suspend fun saveMinDate(minDate: Long) {
        dataStore.edit { prefs ->
            prefs[minDateKey] = minDate
        }
    }

    //Read minDate
    val minDate: Flow<Long?> = dataStore.data.map { prefs ->
        prefs[minDateKey]
    }

    suspend fun saveUserName(userName : String){
        dataStore.edit { prefs ->
            prefs[userNameKey] = userName
        }
    }

    val userName : Flow<String?> = dataStore.data.map { prefs ->
        prefs[userNameKey]
    }




}
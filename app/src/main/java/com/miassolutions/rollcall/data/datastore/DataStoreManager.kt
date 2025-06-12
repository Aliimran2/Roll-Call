package com.miassolutions.rollcall.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStore by preferencesDataStore(name = "user_prefs")

@Singleton
class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val MIN_DATE_KEY = longPreferencesKey("min_date")
    }

    //Save minDate
    suspend fun saveMinDate(minDate: Long) {
        context.dataStore.edit { prefs ->
            prefs[MIN_DATE_KEY] = minDate
        }
    }

    //Read minDate
    val minDate: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[MIN_DATE_KEY]
    }


}
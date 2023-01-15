package io.silv.jikannoto.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.silv.jikannoto.data.util.NotoDispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppDataStoreRepository(
    private val context: Context,
    private val dispatchers: NotoDispatchers
) {
    private val firstNameFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[firstNameKey] ?: ""
        }.flowOn(dispatchers.io)

    private val lastNameFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[lastNameKey] ?: ""
        }.flowOn(dispatchers.io)

    private val syncFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[syncKey] ?: false
        }.flowOn(dispatchers.io)
    fun setFirstName(
        firstname: String
    ) = CoroutineScope(dispatchers.io).launch {
        context.dataStore.edit {
            it[firstNameKey] = firstname
        }
    }
    fun setLastName(
        lastName: String
    ) = CoroutineScope(dispatchers.io).launch {
        context.dataStore.edit {
            it[lastNameKey] = lastName
        }
    }

    val darkThemeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[darkThemeKey] ?: false
        }.flowOn(dispatchers.io)

    fun setDarkTheme(
        isDarkTheme: Boolean
    ) = CoroutineScope(dispatchers.io).launch {
        context.dataStore.edit { preferences ->
            preferences[darkThemeKey] = isDarkTheme
        }
    }

    val collectAllFlow: Flow<DataStoreState> =
        combine(
            firstNameFlow,
            lastNameFlow,
            darkThemeFlow,
            syncFlow
        ) { firstName, lastName, darkTheme, sync ->
            DataStoreState(
                firstname = firstName,
                lastName = lastName,
                darkTheme = darkTheme,
                sync = sync
            )
        }.flowOn(dispatchers.io)
    companion object {
        val firstNameKey = stringPreferencesKey("FIRST_NAME_KEY")
        val lastNameKey = stringPreferencesKey("LAST_NAME_KEY")
        val darkThemeKey = booleanPreferencesKey("DARK_THEME_KEY")
        val syncKey = booleanPreferencesKey("SYNC_KEY")
    }
    data class DataStoreState(
        val firstname: String,
        val lastName: String,
        val darkTheme: Boolean,
        val sync: Boolean = false
    )
}
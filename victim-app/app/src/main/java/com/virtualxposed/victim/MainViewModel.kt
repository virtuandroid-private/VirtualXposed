package com.virtualxposed.victim

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class MainState(
    val secretData: String? = null,
)

val Context.dataStore by preferencesDataStore(name = "private_data")
val SECRET_DATA = stringPreferencesKey("secret_data")

class MainViewModel(val dataStore: DataStore<Preferences>) : ViewModel() {
    private val _state: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state

    init {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                _state.update {
                    it.copy(secretData = preferences[SECRET_DATA])
                }
            }
        }
    }

    fun updateSecretData(editor: (String?) -> String) {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[SECRET_DATA] = editor.invoke(preferences[SECRET_DATA])
            }
        }
    }
}
package com.virtualxposed.victim

import android.os.Build
import androidx.compose.runtime.Immutable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fleeksoft.ksoup.Ksoup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

@Immutable
data class MainState(
    val data: String? = null,
    val version: String? = null,
    val fingerprint: String? = null,
    val fileContent: String? = null
)

class MainViewModel : ViewModel() {
    private val _state: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state

    val client = OkHttpClient()

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val response =
                    client.newCall(Request.Builder().url("https://example.com/").build()).execute()
                val data = Ksoup.parse(response.body.string()).text()

                _state.update {
                    it.copy(data = data)
                }
            }

            _state.update {
                it.copy(version = BuildConfig.VERSION_NAME)
            }
            _state.update {
                it.copy(fingerprint = "${Build.FINGERPRINT}")
            }
        }
    }

    fun createPrivateFile(directory: File) {
        val privateFile = File(directory, "private-file")
        directory.mkdirs()

        if (!privateFile.exists()) {
            privateFile.createNewFile()
            privateFile.writeText("This is the contents of the private file.")
        }

        _state.update {
            it.copy(fileContent = privateFile.readText())
        }
    }
}
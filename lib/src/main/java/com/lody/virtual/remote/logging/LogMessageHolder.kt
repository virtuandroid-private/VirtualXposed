package com.lody.virtual.remote.logging

import android.annotation.SuppressLint
import android.system.Os
import com.lody.virtual.os.VBinder
import com.lody.virtual.server.pm.VPackageManagerService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json

/**
 * This class wraps over the log message to provide additional metadata about the log message.
 *
 * It is assumed that an attacker can send forged log messages to the logging service,
 * therefore this holder determines additional metadata about the sender such as the PID and package name.
 */
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class LogMessageHolder(
    val logMessage: LogMessage?,
    val pid: Int = VBinder.getCallingPid(),
    val vUid: Int = VBinder.getCallingUid(),
    val hostPid: Int = Os.getpid(),
    val packageName: String? = VPackageManagerService().getNameForUid(vUid),
    val timestamp: Long = System.currentTimeMillis(),
) {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val prettyJson = Json {
            prettyPrint = true
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
            encodeDefaults = true
        }
    }

    fun toPrettyJson(): String {
        return prettyJson.encodeToString(this)
    }
}
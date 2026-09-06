package com.lody.virtual.remote.logging

import android.annotation.SuppressLint
import android.system.Os
import com.lody.virtual.client.core.VirtualCore
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
class LogMessageHolder private constructor(
    val logMessage: LogMessage?,
    val pid: Int,
    // val hostPid: Int,
    // TODO Handle cloned package names
    val packageName: String?,
    val timestamp: Long,
) {
    class Builder(private val logMessage: LogMessage) {
        private var pid: Int = VBinder.getCallingPid()

        // private var hostPid: Int = Os.getpid()
        private var packageName: String? =
            VPackageManagerService().getNameForUid(VBinder.getCallingUid())
        private var timestamp: Long = System.currentTimeMillis()

        fun setPid(pid: Int): Builder {
            this.pid = pid
            return this
        }

        fun setPackageName(packageName: String): Builder {
            this.packageName = packageName
            return this
        }

        fun setHostPackage(): Builder {
            if (VirtualCore.get().isServerProcess) {
                this.packageName = VirtualCore.get().processName
                this.pid = Os.getpid()
            }
            return this
        }

        fun build(): LogMessageHolder {
            return LogMessageHolder(logMessage, pid, packageName, timestamp)
        }
    }


    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val prettyJson = Json {
            prettyPrint = false
            classDiscriminatorMode = ClassDiscriminatorMode.NONE
            encodeDefaults = true
        }
    }

    fun toPrettyJson(): String {
        return prettyJson.encodeToString(this)
    }
}
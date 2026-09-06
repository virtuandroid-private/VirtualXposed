package com.lody.virtual.server.log

import com.lody.virtual.remote.logging.LogMessage
import com.lody.virtual.remote.logging.LogMessageHolder
import com.lody.virtual.server.IVLoggingService
import timber.log.Timber

object VLoggingManagerService : IVLoggingService.Stub() {
    @JvmStatic
    fun get() = this
    override fun log(message: LogMessage?) {
        // TODO Replace with file saving
        Timber.i("Received log message: ${LogMessageHolder(message).toPrettyJson()}")
    }
}
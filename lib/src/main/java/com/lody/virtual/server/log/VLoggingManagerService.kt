package com.lody.virtual.server.log

import com.lody.virtual.server.IVLoggingService
import timber.log.Timber

object VLoggingManagerService : IVLoggingService.Stub() {
    override fun log(category: String?, message: String?) {
        Timber.i("Got message: $message from client: ${getCallingPid()} with category: $category")
        // TODO Save to persistent log here
        // TODO Timestamp, PID, Package name, Host PID
    }

    @JvmStatic
    fun get() = this
}
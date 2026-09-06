package com.lody.virtual.server;

import com.lody.virtual.remote.logging.LogMessage;

interface IVLoggingService {
    void log(in LogMessage message);
}
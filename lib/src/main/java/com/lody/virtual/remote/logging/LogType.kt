package com.lody.virtual.remote.logging

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
enum class LogType : Parcelable {
    Hook,
    AppLoad;
}
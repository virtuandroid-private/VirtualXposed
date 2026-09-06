package com.lody.virtual.remote.logging

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator


// Awaiting @PolymorphicSealed from kotlin update to automatically manage parcelable
/** Parcelable message to send to the logging service.
 * Parcelable instead of serializable to increase security against malicious log messages */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed class LogMessage(
    val logType: LogType,
) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(logType, 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<LogMessage> = object : Parcelable.Creator<LogMessage> {
            override fun createFromParcel(parcel: Parcel): LogMessage? {
                val type = parcel.readParcelable<LogType>(LogType::class.java.classLoader)
                return when (type) {
                    LogType.Hook -> HookMessage(parcel)
                    LogType.AppLoad -> AppLoad()
                    LogType.AppKill -> AppKill()
                    null -> null
                }
            }

            override fun newArray(size: Int): Array<LogMessage?> = arrayOfNulls(size)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Serializable
    data class HookMessage(
        val method: String
    ) : LogMessage(LogType.Hook) {
        constructor(parcel: Parcel) : this(parcel.readString()!!)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.writeString(method)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Serializable
    class AppLoad : LogMessage(LogType.AppLoad)


    @OptIn(InternalSerializationApi::class)
    @Serializable
    class AppKill : LogMessage(LogType.AppKill)
}


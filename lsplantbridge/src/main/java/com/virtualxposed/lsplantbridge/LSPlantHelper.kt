package com.virtualxposed.lsplantbridge

import android.os.Binder
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.jvm.javaMethod

class MethodHooker(
    val target: Member,
    private val originalCallback: (originalMethod: Method, args: Array<Any?>) -> Any?
) {
    private val TAG = "MethodHooker"

    // TODO THIS CAN RETURN NULL WHEN DUPLICATE METHOD
    private external fun hookTarget(target: Member, callback: Method): Method
    private external fun unhookTarget(target: Member): Boolean
    lateinit var backup: Method

    // TODO Make mutex locked
    fun hook() {
        backup = hookTarget(target, this::callback.javaMethod!!)
    }

    fun unhook() {
        unhookTarget(target)
    }

    fun callback(args: Array<Any?>): Any? {
        return originalCallback.invoke(backup, args)
    }
}

class LSPlantHelper {
    private val hooks: MutableMap<Member, MethodHooker> = mutableMapOf()

    fun getOriginalMethod(target: Member): Method? {
        return hooks[target]?.backup
    }

    fun hook(
        target: Member,
        callback: (originalMethod: Method, args: Array<Any?>) -> Any
    ): MethodHooker {
        val methodHook = MethodHooker(target, callback)
        methodHook.hook()
        hooks[target] = methodHook
        return methodHook
    }
}
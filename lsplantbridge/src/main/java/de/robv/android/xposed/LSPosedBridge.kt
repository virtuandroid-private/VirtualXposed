package de.robv.android.xposed

import com.virtualxposed.lsplantbridge.LSPlantHelper
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object LSPosedBridge {
    val bridge = LSPlantHelper()

    fun hookMethod(target: Member, callback: XC_MethodHook): XC_MethodHook.Unhook {
        println("Hooking method with LSPosedBridge. Target is: $target")

        return if (target is Method) {
            hookMethod(target, callback)
        } else {
            callback.Unhook(target)
        }
    }

    fun invokeOriginalMethod(method: Member, thisObject: Any?, args: Array<Any?>): Any? {
        return bridge.getOriginalMethod(method)?.invoke(thisObject, args)
    }

    private fun hookMethod(target: Method, callback: XC_MethodHook): XC_MethodHook.Unhook {
        val hooker = bridge.hook(target) { oldMethod, args ->
            val params = XC_MethodHook.MethodHookParam()

            val isStatic = Modifier.isStatic(oldMethod.modifiers)
            val thisObject = if (isStatic) null else args[0]
            val actualArgs = if (isStatic) args else args.sliceArray(1 until args.size)

            params.args = actualArgs
            params.method = oldMethod
            params.thisObject = thisObject

            callback.beforeHookedMethod(params)

            if (params.returnEarly) {
                return@hook params.result
            }

            try {
                val realResult = oldMethod.invoke(thisObject, actualArgs)
                params.result = realResult
            } catch (t: Throwable) {
                params.throwable = t
            }

            callback.afterHookedMethod(params)

            if (params.throwable != null) {
                throw params.throwable
            } else {
                params.result
            }
        }

        return callback.Unhook(hooker.backup)
    }
}
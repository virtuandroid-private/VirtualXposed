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
        return bridge.getOriginalMethod(method)?.invoke(thisObject, *args)
    }

    private fun hookMethod(target: Method, callback: XC_MethodHook): XC_MethodHook.Unhook {
        val hooker = bridge.hook(target) { oldMethod, args ->
            val params = XC_MethodHook.MethodHookParam()

            // TODO Checks for:
            // - Abstract classes
            // - Hooking this code
            // - Recursive hooks (Method.invoke, Constructor.newInstance, getClass)
            val isStatic = Modifier.isStatic(target.modifiers)
            val thisObject = if (isStatic) null else args[0]
            val actualArgs = if (isStatic) args else args.sliceArray(1 until args.size)

            params.args = actualArgs
            params.method = oldMethod
            params.thisObject = thisObject
            runCatching {
                callback.beforeHookedMethod(params)
            }.onFailure { throwable ->
                if (com.virtualxposed.lsplantbridge.BuildConfig.DEBUG) {
                    throwable.printStackTrace()
                }
            }

            if (params.returnEarly) {
                return@hook params.result
            }

            try {
                val realResult = oldMethod.invoke(thisObject, *actualArgs)
                params.result = if (oldMethod.returnType == Void.TYPE) {
                    Unit
                } else {
                    realResult
                }
            } catch (t: Throwable) {
                t.printStackTrace()
                params.throwable = t
            }

            runCatching {
                callback.afterHookedMethod(params)
            }.onFailure { throwable ->
                if (com.virtualxposed.lsplantbridge.BuildConfig.DEBUG) {
                    throwable.printStackTrace()
                }
            }

            val finalResult = if (params.throwable != null) {
                throw params.throwable
            } else {
                params.result
            }

            finalResult ?: Unit
        }

        return callback.Unhook(hooker.backup)
    }
}
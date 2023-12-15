package com.itos.originplan.utils

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import android.os.SystemClock
import android.view.InputEvent
import android.view.KeyEvent
import com.itos.originplan.BuildConfig
import moe.shizuku.server.IShizukuService
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

object OShizuku {
    val myUserId = android.os.Process.myUserHandle().hashCode()
    private val isRoot get() = Shizuku.getUid() == 0
    private val userId get() = if (isRoot) myUserId else 0
    private val callerPackage get() = if (isRoot) BuildConfig.APPLICATION_ID else "com.android.shell"

    private fun asInterface(className: String, serviceName: String): Any =
        ShizukuBinderWrapper(SystemServiceHelper.getSystemService(serviceName)).let {
            Class.forName("$className\$Stub").run {
                if (target(Build.VERSION_CODES.P)) HiddenApiBypass.invoke(
                    this,
                    null,
                    "asInterface",
                    it
                )
                else getMethod("asInterface", IBinder::class.java).invoke(null, it)
            }
        }

    private fun target(api: Int): Boolean = Build.VERSION.SDK_INT >= api
    val lockScreen
        get() = runCatching {
            val input = asInterface("android.hardware.input.IInputManager", "input")
            val inject = input::class.java.getMethod(
                "injectInputEvent", InputEvent::class.java, Int::class.java
            )
            val now = SystemClock.uptimeMillis()
            inject.invoke(
                input, KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_POWER, 0), 0
            )
            inject.invoke(
                input, KeyEvent(now, now, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_POWER, 0), 0
            )
            true
        }.getOrElse {
            false
        }

    private fun forceStopApp(packageName: String) = runCatching {
        asInterface("android.app.IActivityManager", "activity").let {
            if (target(Build.VERSION_CODES.P)) HiddenApiBypass.invoke(
                it::class.java, it, "forceStopPackage", packageName, userId
            ) else it::class.java.getMethod(
                "forceStopPackage", String::class.java, Int::class.java
            ).invoke(
                it, packageName, userId
            )
        }
        true
    }.getOrElse {
        false
    }

    fun setAppDisabled(packageName: String, disabled: Boolean){

        if (disabled) forceStopApp(packageName)
        runCatching {
            val pm = asInterface("android.content.pm.IPackageManager", "package")
            val newState = when {
                !disabled -> PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                isRoot -> PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                else -> PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
            }
            pm::class.java.getMethod(
                "setApplicationEnabledSetting",
                String::class.java,
                Int::class.java,
                Int::class.java,
                Int::class.java,
                String::class.java
            ).invoke(pm, packageName, newState, 0, myUserId, BuildConfig.APPLICATION_ID)
        }.onFailure {
        }

    }

    fun setAppHidden(packageName: String, hidden: Boolean): Boolean {
        if (hidden) forceStopApp(packageName)
        return runCatching {
            val pm = asInterface("android.content.pm.IPackageManager", "package")
            pm::class.java.getMethod(
                "setApplicationHiddenSettingAsUser",
                String::class.java,
                Boolean::class.java,
                Int::class.java
            ).invoke(pm, packageName, hidden, userId) as Boolean
        }.getOrElse {
            false
        }
    }

    fun setAppSuspended(packageName: String, suspended: Boolean): Boolean {
        if (suspended) forceStopApp(packageName)
        return runCatching {
            val pm = asInterface("android.content.pm.IPackageManager", "package")
            (when {
                target(Build.VERSION_CODES.Q) -> HiddenApiBypass.invoke(
                    pm::class.java,
                    pm,
                    "setPackagesSuspendedAsUser",
                    arrayOf(packageName),
                    suspended,
                    null,
                    null,
                    if (suspended) suspendDialogInfo else null,
                    callerPackage,
                    userId
                )

                target(Build.VERSION_CODES.P) -> HiddenApiBypass.invoke(
                    pm::class.java,
                    pm,
                    "setPackagesSuspendedAsUser",
                    arrayOf(packageName),
                    suspended,
                    null,
                    null,
                    null /*dialogMessage*/,
                    callerPackage,
                    userId
                )

                target(Build.VERSION_CODES.N) -> pm::class.java.getMethod(
                    "setPackagesSuspendedAsUser",
                    Array<String>::class.java,
                    Boolean::class.java,
                    Int::class.java
                ).invoke(pm, arrayOf(packageName), suspended, userId)

                else -> return false
            } as Array<*>).isEmpty()
        }.getOrElse {
            false
        }
    }

    private val suspendDialogInfo: Any
        @SuppressLint("PrivateApi") get() = HiddenApiBypass.newInstance(Class.forName("android.content.pm.SuspendDialogInfo\$Builder"))
            .let {
                HiddenApiBypass.invoke(
                    it::class.java, it, "setNeutralButtonAction", 1 /*BUTTON_ACTION_UNSUSPEND*/
                )
                HiddenApiBypass.invoke(it::class.java, it, "build")
            }

    fun execute(command: String, root: Boolean = isRoot): Pair<Int, String?> = runCatching {
        IShizukuService.Stub.asInterface(Shizuku.getBinder())
            .newProcess(arrayOf(if (root) "su" else "sh"), null, null).run {
                ParcelFileDescriptor.AutoCloseOutputStream(outputStream).use {
                    it.write(command.toByteArray())
                }
                waitFor() to inputStream.text.ifBlank { errorStream.text }.also { destroy() }
            }
    }.getOrElse { 0 to it.stackTraceToString() }

    private val ParcelFileDescriptor.text
        get() = ParcelFileDescriptor.AutoCloseInputStream(this)
            .use { it.bufferedReader().readText() }
}
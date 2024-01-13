package com.itos.xplan.utils

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

object OPackage {
    fun isInstalled(packageName: String, packageManager: PackageManager): Boolean {
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            OLog.i("应用安装判断", "$packageName 已安装")
            return packageInfo != null
        } catch (ep: Throwable) {
            OLog.i("应用安装判断", "$packageName 未安装")
        }
        return false
    }

    fun getAppIconByPackageName(packageName: String, packageManager: PackageManager): Drawable? {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationIcon(applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}
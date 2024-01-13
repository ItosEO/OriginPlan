package com.itos.originplan.utils

import android.content.pm.PackageManager

object OPackage {
    fun isInstalled(packageName: String,pm: PackageManager): Boolean {
        try {
            val packageInfo = pm.getPackageInfo(packageName, 0)
            OLog.i("应用安装判断", "$packageName 已安装")
            return packageInfo != null
        } catch (ep: Throwable) {
            OLog.i("应用安装判断", "$packageName 未安装")
        }
        return false
    }
}
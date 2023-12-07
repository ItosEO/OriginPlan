package com.itos.originplan

import android.content.pm.PackageManager
import android.widget.Toast

import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import kotlin.random.Random

object ShizukuHelper {

    fun getSelinuxContext(): String {
        return try {
            Shizuku.getSELinuxContext()!!
        } catch (err: Throwable) {
            "null"
        }
    }

    fun checkPermission(): Boolean {
        return try {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (err: Throwable) {
            false
        }
    }

    fun requestPermission(callback: (result: Int) -> Unit) {
        try {
            if (checkPermission()) return
            val requestCode = Random.nextInt()
            val listener = object : Shizuku.OnRequestPermissionResultListener {
                override fun onRequestPermissionResult(code: Int, result: Int) {
                    Shizuku.removeRequestPermissionResultListener(this)
                    if (code == requestCode) {
                        callback(result)
                    }
                }
            }
            Shizuku.addRequestPermissionResultListener(listener)
            if (!Shizuku.shouldShowRequestPermissionRationale()) {
                Shizuku.requestPermission(requestCode)
            }
        } catch (_: Exception){ }
    }


}
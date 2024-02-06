package com.itos.xplan.crashcatch
import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashHandler.getInstance(this)
    }
}
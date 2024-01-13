package com.itos.xplan.utils

import android.util.Log

object OLog {
    fun i(tag: String, msg: String) = Log.i(tag, msg)
    fun e(tag:String, t: Throwable) = Log.e(tag, t.stackTraceToString())
}
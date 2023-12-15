package com.itos.originplan.utils

import android.util.Log

object OLog {
    private const val TAG = "OriginPlan"
    fun i(tag: String, string: String) = Log.i(tag, string)
    fun e(t: Throwable) = Log.e(TAG, t.stackTraceToString())
}
package com.itos.xplan.datatype

import android.graphics.drawable.Drawable

data class AppInfo(
    var appName: String,
    val appPkg: String,
    var appIcon: Drawable?=null,
    var isDisabled: Boolean = false,
    var isExist: Boolean = true
)
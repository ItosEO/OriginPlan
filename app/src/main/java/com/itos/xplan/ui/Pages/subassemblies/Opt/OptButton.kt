package com.itos.xplan.ui.Pages.subassemblies.Opt

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itos.xplan.XPlan.Companion.app
import com.itos.xplan.utils.OShizuku

@Composable
fun OptButton(){
    Row(
        modifier = Modifier
            .padding(vertical = 45.dp)
    ) {
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = { opt_setappstauts(false) }
        ) {
            Text("一键优化")
        }
        Spacer(modifier = Modifier.width(25.dp))
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = { opt_setappstauts(true) }
        ) {
            Text("还原")
        }
    }
}

fun opt_setappstauts(status: Boolean) {
    if (app.isShizukuStart && app.isShizukuAuthorized) {
        app.generateAppList(app)
        // 遍历app list
        for (appInfo in app.optlist) {
            if (appInfo.isExist) {
                app.SetAppDisabled(mutableStateOf(status), appInfo.appPkg, appInfo.isExist, false)
                appInfo.isDisabled = app.isAppDisabled(appInfo.appPkg)
            }
        }
        if (!status) {
            MaterialAlertDialogBuilder(app)
                .setTitle("完成")
                .setMessage("一键优化完成")
                .setPositiveButton(android.R.string.ok, null)
                .show()
        } else {
            MaterialAlertDialogBuilder(app)
                .setTitle("完成")
                .setMessage("还原完成")
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
        app.generateAppList(app)
    } else {
        OShizuku.checkShizuku()
    }
}

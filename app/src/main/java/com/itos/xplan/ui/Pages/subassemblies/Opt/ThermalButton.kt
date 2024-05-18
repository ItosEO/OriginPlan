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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itos.xplan.XPlan.Companion.app
import com.itos.xplan.utils.OShizuku

fun SettingsOpt(){
    OShizuku.setAppDisabled("com.vivo.sps", true)
    OShizuku.setAppDisabled("com.android.pacprocessor", true)
    OShizuku.setAppDisabled("com.vivo.pem", true)
    MaterialAlertDialogBuilder(app)
        .setTitle("完成")
        .setMessage("调整完成")
        .setPositiveButton("OK",null)
        .show()
}
fun SettingsRestore (){
    OShizuku.setAppDisabled("com.vivo.sps", false)
    OShizuku.setAppDisabled("com.android.pacprocessor", false)
    OShizuku.setAppDisabled("com.vivo.pem", false)
    MaterialAlertDialogBuilder(app)
        .setTitle("完成")
        .setMessage("还原完成")
        .setPositiveButton("OK",null)
        .show()
}

@Composable
fun Thermal_opt() {
    Row(
        modifier = Modifier
            .padding(vertical = 45.dp)
    ) {
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = {
                SettingsOpt()
            }
        ) {
            Text("屏蔽\n温控组件", textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.width(25.dp))
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = {
                SettingsRestore()
            }
        ) {
            Text("还原\n温控组件", textAlign = TextAlign.Center)
        }
//        Spacer(modifier = Modifier.width(15.dp))
//        FilledTonalButton(
//            modifier = Modifier
//                .size(width = 80.dp, height = 60.dp),
//            shape = RoundedCornerShape(30),
//            onClick = {
//                SettingsDebug()
//                Toast.makeText(app, "开发中...", Toast.LENGTH_SHORT).show()
//            }
//        ) {
//            Text("调试", textAlign = TextAlign.Center)
//        }
    }
}
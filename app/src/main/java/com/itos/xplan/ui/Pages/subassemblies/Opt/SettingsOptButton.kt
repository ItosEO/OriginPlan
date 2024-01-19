package com.itos.xplan.ui.Pages.subassemblies.Opt

import android.widget.Toast
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
import com.itos.xplan.XPlan.Companion.app
import android.provider.Settings;
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itos.xplan.utils.OData
import com.itos.xplan.utils.OLog
import com.itos.xplan.utils.OPackage
import com.itos.xplan.utils.SpUtils

fun SettingsOpt(){
//    if (OData.is_have_premissipn){
//        OData.configdata.data.forEach { innerList ->
////            Settings.System.putString(app.contentResolver, innerList[0], Integer.valueOf(innerList[1]))
//            SpUtils.putSettingsParam(app, innerList[2], innerList[0], innerList[1])
//            OLog.i("系统参数调优",innerList.toString())
//        }
//    }else{
//        Toast.makeText(app,"权限不足",Toast.LENGTH_SHORT).show()
//    }
    app.ShizukuExec(OData.configdata.shell.toByteArray())
    MaterialAlertDialogBuilder(app)
        .setTitle("完成")
        .setMessage("调整完成")
        .setPositiveButton("OK",null)
        .show()
}
fun SettingsRestore (){
//    if (OData.is_have_premissipn){
//        OData.configdata.data.forEach { innerList ->
////            Settings.System.putString(app.contentResolver, innerList[0], Integer.valueOf(innerList[1]))
//            SpUtils.putSettingsParam(app, innerList[2], innerList[0], innerList[1])
//            OLog.i("系统参数调优",innerList.toString())
//        }
//    }else{
//        Toast.makeText(app,"权限不足",Toast.LENGTH_SHORT).show()
//    }
    app.ShizukuExec(OData.configdata.restore.toByteArray())
    MaterialAlertDialogBuilder(app)
        .setTitle("完成")
        .setMessage("还原完成")
        .setPositiveButton("OK",null)
        .show()
}

@Composable
fun Settings_opt() {
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
                Toast.makeText(app, "开发中...", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text("系统参数\n调优", textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.width(25.dp))
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = {
                SettingsRestore()
                Toast.makeText(app, "开发中...", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text("还原\n系统参数", textAlign = TextAlign.Center)
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
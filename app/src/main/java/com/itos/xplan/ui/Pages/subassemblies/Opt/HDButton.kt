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
import androidx.compose.ui.unit.dp
import com.itos.xplan.utils.OLog
import com.itos.xplan.utils.OShizuku.ShizukuExec_US

@Composable
fun HDButton() {
    Row(
        modifier = Modifier
            .padding(vertical = 45.dp)
    ) {
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = { HideHD() }
        ) {
            Text("隐藏HD")
        }
        Spacer(modifier = Modifier.width(25.dp))
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = { UnHideHD() }
        ) {
            Text("还原HD")
        }
    }
}

fun HideHD() {
    var data = ShizukuExec_US("settings get secure icon_blacklist") ?: ""
    val items = if (data.isNotEmpty()) data.split(",").toMutableList() else mutableListOf()

    if (!items.contains("rotate")) {
        items.add(0, "rotate")
    }
    if (!items.contains("hd")) {
        items.add(0, "hd")
    }
    data = items.joinToString(",")
    OLog.i("隐藏HD", data)
    ShizukuExec_US ("settings put secure icon_blacklist $data")

}

fun UnHideHD() {
    var data = ShizukuExec_US("settings get secure icon_blacklist") ?: ""
    val items = if (data.isNotEmpty()) data.split(",").toMutableList() else mutableListOf()

    items.removeAll(listOf("rotate", "hd"))
    data = items.joinToString(",")
    OLog.i("还原HD", data)

    ShizukuExec_US("settings put secure icon_blacklist $data")
}


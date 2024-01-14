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
import com.itos.xplan.utils.OData
import com.itos.xplan.utils.OLog

fun SettingsOpt(){
    if (OData.is_have_premissipn){
        OData.configdata.data.forEach { innerList ->
            innerList.forEach { value ->
                OLog.i("系统参数调优",value)
            }
        }
    }else{
        Toast.makeText(app,"权限不足",Toast.LENGTH_SHORT).show()
    }
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
            Text("系统参数调优", textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.width(25.dp))
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = {
                Toast.makeText(app, "开发中...", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text("还原\n系统参数", textAlign = TextAlign.Center)
        }
    }
}
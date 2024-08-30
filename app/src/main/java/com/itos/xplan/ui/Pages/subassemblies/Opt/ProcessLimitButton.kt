package com.itos.xplan.ui.Pages.subassemblies.Opt

import android.annotation.SuppressLint
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

@SuppressLint("StaticFieldLeak")

@Composable
fun ProcessLimitButton(){
    Row(
        modifier = Modifier
            .padding(vertical = 45.dp)
    ) {
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = {
                app.patchProcessLimit()
            }
        ) {
            Text("一键开启高刷应用", textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.width(25.dp))
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = {
//                app.unpatchProcessLimit()
                Toast.makeText(app, "暂不支持", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text("***", textAlign = TextAlign.Center)
        }
    }
}
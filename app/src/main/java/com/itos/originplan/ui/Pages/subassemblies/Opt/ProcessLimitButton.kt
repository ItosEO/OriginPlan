package com.itos.originplan.ui.Pages.subassemblies.Opt

import android.annotation.SuppressLint
import android.content.Context
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
import com.itos.originplan.MainActivity

@SuppressLint("StaticFieldLeak")

@Composable
fun ProcessLimitButton(context:Context){
    Row(
        modifier = Modifier
            .padding(vertical = 45.dp)
    ) {
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = {
                (context as? MainActivity)?.patchProcessLimit()
            }
        ) {
            Text("调整Android进程设置", textAlign = TextAlign.Center)
        }
        Spacer(modifier = Modifier.width(25.dp))
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = {
                (context as? MainActivity)?.unpatchProcessLimit()
            }
        ) {
            Text("还原\n进程设置", textAlign = TextAlign.Center)
        }
    }
}
package com.itos.originplan.ui.Pages.subassemblies

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
import androidx.compose.ui.unit.dp
import com.itos.originplan.MainActivity

@Composable
fun OptButton(context: Context){
    Row(
        modifier = Modifier
            .padding(vertical = 45.dp)
    ) {
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = { (context as? MainActivity)?.opt_setappstauts(false) }
        ) {
            Text("一键优化")
        }
        Spacer(modifier = Modifier.width(25.dp))
        FilledTonalButton(
            modifier = Modifier
                .size(width = 130.dp, height = 60.dp),
            shape = RoundedCornerShape(30),
            onClick = { (context as? MainActivity)?.opt_setappstauts(true) }
        ) {
            Text("还原")
        }
    }
}
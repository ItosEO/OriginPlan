package com.itos.originplan

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itos.originplan.ui.Pages.subassemblies.OptButton
import com.itos.originplan.ui.Pages.subassemblies.ProcessLimitButton
import com.itos.originplan.ui.Pages.subassemblies.Settings_opt

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptPage(context: Context) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(text = "优化")
            },
            actions = {
                IconButton(
                    onClick = {
                        MaterialAlertDialogBuilder(context)
                            .setTitle("公告")
                            .setMessage((context as? MainActivity)?.show_notice)
                            .setPositiveButton("了解") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "help"
                    )
                }
            }
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // 将子项垂直居中
        ) {
            OptButton(context)
            ProcessLimitButton(context)
            Settings_opt(context)
        }

    }
}
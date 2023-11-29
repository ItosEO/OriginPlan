package com.itos.study_kotlin

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itos.study_kotlin.ui.theme.Study_kotlinTheme

data class AppInfo(
    val appName: String,
    val appPkg: String,
    var isDisabled: Boolean
)
@Composable
fun AppListItem(appInfo: AppInfo, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左边显示应用名称
        Column {
            Text(text = appInfo.appName, style = MaterialTheme.typography.bodyMedium)
            Text(text = appInfo.appPkg, style = MaterialTheme.typography.bodySmall)
        }

        // 中间显示禁用状态文本
        Text(
            text = if (appInfo.isDisabled) "Disabled" else "Enabled",
            color = if (appInfo.isDisabled) Color.Red else Color.Green,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 16.dp)
        )

        // 右边是一个按钮
        IconButton(
            onClick = onToggle
        ) {
            if (appInfo.isDisabled) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Enable")
            } else {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Disable")
            }
        }
    }
}




@Composable
fun AppList(appList: List<AppInfo>) {
    LazyColumn {
        items(appList) { appInfo ->
            AppListItem(
                appInfo = appInfo,
                onToggle = { appInfo.isDisabled = !appInfo.isDisabled }
            )
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen() {
    val appList = remember { generateAppList() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "App List") },
                // backgroundColor = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        AppList(appList = appList)
    }
}

@Composable
fun AppListContent() {
    AppListScreen()
}

fun generateAppList(): List<AppInfo> {
    // 这里添加你的应用信息
   // 返回一个长度为10的列表，其中包含10个AppInfo对象
    return List(10) { index ->
        AppInfo(
            // 根据索引值，生成不同的应用名称
            appName = "App $index",
            // 根据索引值，生成不同的应用包名
            appPkg = "com.example.app$index",
            // 根据索引值，判断应用是否被禁用
            isDisabled = index % 2 == 0
        )
    }
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Study_kotlinTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SetTitle("原计划")
                    AppListContent()
                }
            }
        }
    }
}

@Composable
fun SetTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
    )
}

// 添加中文注释
@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GreetingPreview() {
    Study_kotlinTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            SetTitle("原计划")
            AppListContent()
        }
    }
}
package com.itos.originplan

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.itos.originplan.ui.theme.Study_kotlinTheme


data class AppInfo(
    var appName: String,
    val appPkg: String,
    var isDisabled: Boolean = false
)

val pkglist: List<AppInfo> = listOf(
    AppInfo("vivoqwk", "bin.mt.plus.canary"),
    //AppInfo("vivoopt", "com.itos.optizimation"),
)

@Composable
fun AppListItem(appInfo: AppInfo, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            //.heightIn(min = 48.dp)
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


fun getAppNameByPackageName(context: Context, packageName: String): String {
    val packageManager: PackageManager = context.packageManager
    val applicationInfo: ApplicationInfo? = try {
        packageManager.getApplicationInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }

    return applicationInfo?.let {
        packageManager.getApplicationLabel(it).toString()
    } ?: "Unknown App"
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
fun AppListScreen(context: Context) {
    val appList = remember { generateAppList(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "原计划") },
                // backgroundColor = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        Column {
            // TopAppBar
            TopAppBar(
                title = { Text(text = "原计划") },
                // backgroundColor = MaterialTheme.colorScheme.primary
            )

            // AppList
            AppList(appList = appList)
        }
    }

}

@Composable
fun AppListContent() {
    AppListScreen(LocalContext.current)
}

fun generateAppList(context: Context): List<AppInfo> {
    // 这里添加你的应用信息
    // 返回一个长度为10的列表，其中包含10个AppInfo对象
    for (appinfo in pkglist) {
        appinfo.isDisabled = isAppDisabled(context, appinfo.appPkg)
        appinfo.appName = getAppNameByPackageName(context, appinfo.appPkg)
    }
    val testlist: List<AppInfo> = List(2) { index ->
        AppInfo(
            appName = "App $index",
            appPkg = "com.example.app$index",
            isDisabled = index % 2 == 0
        )
    }
    Log.d("列表项", pkglist.toString()+"\n"+testlist.toString())
    return testlist
}


fun isAppDisabled(context: Context, appPackageName: String): Boolean {
    val packageManager: PackageManager = context.packageManager

    try {
        val applicationEnabledSetting: Int =
            packageManager.getApplicationEnabledSetting(appPackageName)

        // 应用被停用或者处于默认状态（未设置启用状态），返回 true；其他状态返回 false
        if (applicationEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            return true
        } else if (applicationEnabledSetting == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
            return false
        }
    } catch (e: PackageManager.NameNotFoundException) {
        // 如果找不到应用信息，也可以视为应用被停用
        return true
    }
    return true
}
class MainActivity : ComponentActivity() {
    val context: Context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Study_kotlinTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // SetTitle("原计划")
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
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
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
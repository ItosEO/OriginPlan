package com.itos.originplan

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.text.util.Linkify
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.itos.originplan.ui.theme.Study_kotlinTheme
import com.itos.originplan.utils.OLog
import com.itos.originplan.utils.OShizuku
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnBinderReceivedListener
import rikka.shizuku.Shizuku.UserServiceArgs
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream

// TODO 改全局主题色


data class AppInfo(
    var appName: String,
    val appPkg: String,
    var isDisabled: Boolean = false,
    var isExist: Boolean = true
)


class MainActivity : ComponentActivity() {
    private val context: Context = this
    var userService: IUserService? = null
    var a: Boolean = false
    val pkglist: List<AppInfo> = listOf(
        AppInfo("mt", "bin.mt.plus.canary"),
        AppInfo("origin read", "com.vivo.newsreader"),
        AppInfo("douyin", "com.ss.android.ugc.aweme"),

        )
    var ReturnValue: Int = 0
    val REQUEST_CODE = 123
    val userServiceArgs = UserServiceArgs(
        ComponentName(
            BuildConfig.APPLICATION_ID,
            UserService::class.java.name
        )
    ).processNameSuffix("service")
    val userServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            if (service.pingBinder()) {
                userService = IUserService.Stub.asInterface(service)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }
    private val requestPermissionResultListener =
        Shizuku.OnRequestPermissionResultListener { requestCode: Int, grantResult: Int ->
            this.onRequestPermissionsResult(
                requestCode,
                grantResult
            )
        }
    private val BINDER_RECEVIED_LISTENER =
        object : OnBinderReceivedListener {
            override fun onBinderReceived() {
                Toast.makeText(
                    context,
                    checkPermission().toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    private val BINDER_DEAD_LISTENER: Shizuku.OnBinderDeadListener =
        object : Shizuku.OnBinderDeadListener {
            override fun onBinderDead() {
                Toast.makeText(
                    context,
                    checkPermission().toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Study_kotlinTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppListContent()
                }
            }
        }

        Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
        try {
            if (checkPermission(REQUEST_CODE)) {
                //onGranted()
            } else {
                Shizuku.requestPermission(REQUEST_CODE)
            }
        } catch (_: Exception) {
        }
        Shizuku.addBinderReceivedListenerSticky(BINDER_RECEVIED_LISTENER)
        Shizuku.addBinderDeadListener(BINDER_DEAD_LISTENER)
        Shizuku.bindUserService(userServiceArgs, userServiceConnection)
        Toast.makeText(context, "shizuku:" + checkPermission().toString(), Toast.LENGTH_SHORT)
            .show()
    }

    private fun onRequestPermissionsResult(requestCode: Int, grantResult: Int) {
        Toast.makeText(context, "shizuku:" + checkPermission().toString(), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.unbindUserService(userServiceArgs, userServiceConnection, true)
        Shizuku.removeBinderReceivedListener(BINDER_RECEVIED_LISTENER)
        Shizuku.removeBinderDeadListener(BINDER_DEAD_LISTENER)
        Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
    }

    fun ShizukuExec(cmd: ByteArray): String? {
        val op = arrayOfNulls<String>(1)
        try {
            OLog.i("运行shell", "开始运行$cmd")
            val p = Shizuku.newProcess(arrayOf<String>("sh"), null, null)
            val out: OutputStream = p.getOutputStream()
            out.write(cmd)
            out.flush()
            out.close()
            val h2 = Thread {
                try {
                    val outText = StringBuilder()
                    val reader = BufferedReader(InputStreamReader(p.getInputStream()))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        outText.append(line).append("\n")
                    }
                    reader.close()
                    val output = outText.toString()
                    OLog.i("Output_Normal", output)
                    op[0] = output
                } catch (ignored: java.lang.Exception) {
                }
            }
            h2.start()
            val h3 = Thread {
                try {
                    val outText = StringBuilder()
                    val reader = BufferedReader(InputStreamReader(p.getErrorStream()))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        outText.append(line).append("\n")
                    }
                    reader.close()
                    val output = outText.toString()
                    OLog.i("Output_Error", output)
                } catch (ignored: java.lang.Exception) {
                }
            }
            h3.start()
            OLog.i("运行shell,shizuku", "开始等待")
            p.waitFor()
            ReturnValue = p.exitValue()
            OLog.i("运行shell,shizuku", "跑完了")
            p.destroyForcibly()
            val m = Message()
            m.what = 2
            m.obj = "完成！"
            return op[0]
        } catch (ignored: java.lang.Exception) {
        }
        return null
    }

    private fun checkPermission(code: Int): Boolean {
        if (Shizuku.isPreV11()) {
            // Pre-v11 is unsupported
            return false
        }
        if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            // Granted
            return true
        } else if (Shizuku.shouldShowRequestPermissionRationale()) {
            // Users choose "Deny and don't ask again"
            return false
        } else {
            return false
        }
    }

    fun SetAppDisabled(isDisabled: MutableState<Boolean>, packagename: String, isExist: Boolean) {
        Toast.makeText(
            context,
            packagename + ": " + isDisabled.value.toString(),
            Toast.LENGTH_SHORT
        ).show()
        if (isExist) {
            OShizuku.setAppDisabled(packagename, !isDisabled.value)
            isDisabled.value = isAppDisabled(packagename)!!
            Toast.makeText(context, isDisabled.value.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun checkPermission(): Boolean {
        return try {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (err: Throwable) {
            false
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

    fun openLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    private fun isAppDisabled(appPackageName: String): Boolean {
        val packageManager: PackageManager = context.packageManager

        val packageInfo = packageManager.getPackageInfo(appPackageName, 0)
        // 应用被停用或者处于默认状态（未设置启用状态），返回 true；其他状态返回 false
        return !packageInfo.applicationInfo.enabled
    }

    private fun isInstalled(packageName: String): Boolean {
        val pm = context.packageManager;
        try {
            val packageInfo = pm.getPackageInfo(packageName, 0)
            OLog.i("应用安装判断", "$packageName 已安装")
            return packageInfo != null;
        } catch (ep: Throwable) {
            OLog.i("应用安装判断", "$packageName 未安装")
            OLog.e("应用安装判断报错", ep)
        }
        return false;
    }

    @Preview(showBackground = true)
    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun GreetingPreview() {
        Study_kotlinTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                //SetTitle("原计划")
                AppListContent()
            }
        }
    }


    @Composable
    fun AppListItem(appInfo: AppInfo) {
        //让 compose监听这个的变化
        var isDisabled = remember { mutableStateOf(appInfo.isDisabled) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左边显示应用名称
            Column(modifier = Modifier.weight(0.5f)) {
                Text(text = appInfo.appName, style = MaterialTheme.typography.bodyMedium)
                Text(text = appInfo.appPkg, style = MaterialTheme.typography.bodySmall)
            }

            // 中间显示禁用状态文本
            Text(
                text = if (!appInfo.isExist) "Unknow" else if (isDisabled.value) "Disable" else "Enable",
                color = if (!appInfo.isExist) Color(0xFFFF6E40) else if (isDisabled.value) Color(
                    0xFFFF5252
                ) else Color(0xFF69F0AE),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 16.dp)
            )

            // 右边是一个按钮
            IconButton(
                onClick = { SetAppDisabled(isDisabled, appInfo.appPkg, appInfo.isExist) }
            ) {
                val icon: ImageVector = if (appInfo.isExist && isDisabled.value) {
                    Icons.Default.Check
                } else if (appInfo.isExist) {
                    Icons.Default.Close
                } else {
                    Icons.Default.Warning
                }
                // icon = if (isDisabled) Icons.Default.Check else Icons.Default.Close
                Icon(
                    imageVector = icon,

                    contentDescription = if (!appInfo.isExist) "Unknow" else if (isDisabled.value) "Disable" else "Enable"
                )
            }

        }
    }


    @Composable
    fun AppList(appList: List<AppInfo>) {
        LazyColumn {
            items(appList) { appInfo ->
                AppListItem(
                    appInfo = appInfo
                )
            }
        }
    }

    private fun showLicenses() {
        // TODO 改颜色
        // val customContext = ContextThemeWrapper(context, R.style.Theme_MDialog)
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.action_licenses)
            .setMessage(resources.openRawResource(R.raw.licenses).bufferedReader().readText())
            .setPositiveButton(android.R.string.ok, null)
            .show()
            .findViewById<MaterialTextView>(android.R.id.message)?.apply {
                setTextIsSelectable(true)
                Linkify.addLinks(this, Linkify.EMAIL_ADDRESSES or Linkify.WEB_URLS)
                // The first time the link is clicked the background does not change color and
                // the view needs to get focus once.
                requestFocus()
            }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppListScreen(context: Context) {
        val appList = remember { generateAppList(context) }
        var expanded by remember { mutableStateOf(false) }
        Scaffold {
            Column {
                // TopAppBar
                TopAppBar(

                    title = { Text(text = "OriginPlan") },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        titleContentColor = Color.White,
                        containerColor = Color(android.graphics.Color.parseColor("#212121"))
                    ),
                    actions = {
                        IconButton(
                            onClick = { expanded = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "菜单"
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            // 添加菜单项
                            DropdownMenuItem(
                                text = { Text(text = "GitHub") },
//                                colors = MenuDefaults.itemColors(textColor = Color.White),
                                onClick = {
                                    expanded =
                                        false; openLink("https://github.com/ItosEO/OriginPlan")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.Companion.vectorResource(R.drawable.ic_outline_code),
                                        contentDescription = "GitHub"
                                    )
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = "许可证") },
                                onClick = { expanded = false; showLicenses() },
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.Companion.vectorResource(R.drawable.ic_outline_lisence),
                                        contentDescription = "GitHub"
                                    )
                                }
                            )

                            // 添加更多菜单项...
                        }
                    }
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
        for (appinfo in pkglist) {
            if (isInstalled(appinfo.appPkg)) {
                appinfo.appName = getAppNameByPackageName(context, appinfo.appPkg)
                a = isAppDisabled(appinfo.appPkg)
                appinfo.isDisabled = a
            } else {
                appinfo.isExist = false
            }
        }
//        val testlist: List<AppInfo> = List(2) { index ->
//            AppInfo(
//                appName = "App $index",
//                appPkg = "com.example.app$index",
//                isDisabled = index % 2 == 0
//            )
//        }
        OLog.i("列表项", pkglist.toString())
        return pkglist
    }

}




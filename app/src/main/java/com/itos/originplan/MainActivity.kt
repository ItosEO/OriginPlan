package com.itos.originplan

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.util.Linkify
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.windowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alibaba.fastjson.JSONObject
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.itos.originplan.ui.theme.OriginPlanTheme
import com.itos.originplan.utils.NetUtils
import com.itos.originplan.utils.OLog
import com.itos.originplan.utils.OShizuku
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnBinderReceivedListener
import rikka.shizuku.ShizukuRemoteProcess
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream


data class AppInfo(
    var appName: String,
    val appPkg: String,
    var isDisabled: Boolean = false,
    var isExist: Boolean = true
)

data class OriginCardItem(
    val icon: ImageVector? = null,
    val label: String,
    val content: String? = null,
    val onClick: (() -> Unit)? = null
)

// TODO 拆分About页面
class MainActivity : AppCompatActivity() {
    private val context: Context = this
    var ReturnValue = 0
    var br = false
    var h2: Thread? = null
    var h3: Thread? = null
    var b = true
    var c = false
    private val pkglist = mutableListOf<AppInfo>()
    private val optlist = mutableListOf<AppInfo>()
    //var userService: IUserService? = null

//    val userServiceArgs = UserServiceArgs(
//        ComponentName(
//            BuildConfig.APPLICATION_ID,
//            UserService::class.java.name
//        )
//    ).processNameSuffix("service")
//    val userServiceConnection: ServiceConnection = object : ServiceConnection {
//        override fun onServiceConnected(name: ComponentName, service: IBinder) {
//            if (service.pingBinder()) {
//                userService = IUserService.Stub.asInterface(service)
//            }
//        }
//
//        override fun onServiceDisconnected(name: ComponentName) {}
//    }

    private val requestPermissionResultListener =
        Shizuku.OnRequestPermissionResultListener { requestCode: Int, grantResult: Int ->
            this.onRequestPermissionsResult()
        }
    private val BINDER_RECEVIED_LISTENER =
        OnBinderReceivedListener {
            checkShizuku()
        }
    private val BINDER_DEAD_LISTENER: Shizuku.OnBinderDeadListener =
        Shizuku.OnBinderDeadListener {
            checkShizuku()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OriginPlanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppListContent()
                }
            }
        }
        try {
            // 打开 pkglistfile 文件输入流
            val inputStream_pkg = resources.openRawResource(R.raw.pkglist)
            val reader_pkg = BufferedReader(InputStreamReader(inputStream_pkg))
            val inputStream_opt = resources.openRawResource(R.raw.optlist)
            val reader_opt = BufferedReader(InputStreamReader(inputStream_opt))

            // 逐行读取文件内容
            var line_pkg: String?
            var line_opt: String?
            while (reader_pkg.readLine().also { line_pkg = it } != null) {
                val packageName = line_pkg!!.trim()
                // 创建 AppInfo 对象，并添加到列表
                val appInfo = AppInfo(appName = "", appPkg = packageName)
                pkglist.add(appInfo)
            }
            while (reader_opt.readLine().also { line_opt = it } != null) {
                val packageName = line_opt!!.trim()
                // 创建 AppInfo 对象，并添加到列表
                val appInfo = AppInfo(appName = "", appPkg = packageName)
                optlist.add(appInfo)
            }
        } catch (e: Exception) {
            // 处理异常，例如文件不存在等情况
            e.printStackTrace()
        }

        Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
        checkShizuku()
        Shizuku.addBinderReceivedListenerSticky(BINDER_RECEVIED_LISTENER)
        Shizuku.addBinderDeadListener(BINDER_DEAD_LISTENER)

        update_notice()

//        Shizuku.bindUserService(userServiceArgs, userServiceConnection)
    }

    private fun opt_setappstauts(status: Boolean) {
        // 遍历app list
        for (appInfo in optlist) {
            if (appInfo.isExist) SetAppDisabled(
                mutableStateOf(status),
                appInfo.appPkg,
                appInfo.isExist
            )
        }
        if (!status) {
            MaterialAlertDialogBuilder(context)
                .setTitle("完成")
                .setMessage("一键优化完成")
                .setPositiveButton(android.R.string.ok, null)
                .show()
        } else {
            MaterialAlertDialogBuilder(context)
                .setTitle("完成")
                .setMessage("还原完成")
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

    fun update_notice() {
        lifecycleScope.launch(Dispatchers.IO) {
            // 后台工作
            val update = NetUtils.Get("https://itos.codegang.top/share/originplan/app_update.json")
            // 切换到主线程进行 UI 操作
            withContext(Dispatchers.Main) {
                // UI 操作，例如显示 Toast
                val jsonObject = JSONObject.parseObject(update)
                val version = jsonObject.getString("version")
                val url = jsonObject.getString("url")
                val version_name = jsonObject.getString("version_name")
                val log = jsonObject.getString("log")
                val isShowNotice = jsonObject.getBoolean("isShowNotice")
                val notice = jsonObject.getString("notice")
                OLog.i(
                    "更新",
                    update + "\n" + version + "\n" + url + "\n" + version_name + "\n" + log + "\n" + isShowNotice + "\n" + notice
                )
                if (BuildConfig.VERSION_CODE < version.toInt()) {
                    OLog.i("更新", "有新版本")
                    MaterialAlertDialogBuilder(context)
                        .setTitle("有新版本")
                        .setMessage("最新版本：$version_name($version)\n\n更新日志：\n$log")
                        .setPositiveButton("前往更新") { dialog, which ->
                            openLink(url)
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                } else {
                    if (isShowNotice) {
                        OLog.i("公告", "显示")
                        MaterialAlertDialogBuilder(context)
                            .setTitle("公告")
                            .setMessage(notice)
                            .setPositiveButton("我知道了") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                }

            }
        }
    }

    private fun checkShizuku() {
//        var b = true
//        var c = false
        try {
            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) Shizuku.requestPermission(
                0
            ) else c = true
        } catch (e: java.lang.Exception) {
            if (checkSelfPermission("moe.shizuku.manager.permission.API_V23") == PackageManager.PERMISSION_GRANTED) c =
                true
            if (e.javaClass == IllegalStateException::class.java) {
                b = false
            }
        }
        if (!b || !c) {
            Toast.makeText(
                this,
                "Shizuku " + (if (b) "已运行" else "未运行") + if (c) " 已授权" else " 未授权",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun onRequestPermissionsResult() {
        checkShizuku()
    }

    override fun onDestroy() {
        super.onDestroy()
//        Shizuku.unbindUserService(userServiceArgs, userServiceConnection, true)
        Shizuku.removeBinderReceivedListener(BINDER_RECEVIED_LISTENER)
        Shizuku.removeBinderDeadListener(BINDER_DEAD_LISTENER)
        Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
    }

    private fun uninstall(appInfo: AppInfo, a:MutableState<Boolean>) {

        MaterialAlertDialogBuilder(context)
            .setTitle("尝试卸载")
            .setMessage("您将卸载 ${appInfo.appName}(${appInfo.appPkg})")
            .setPositiveButton("确定") { _, _ ->
                val t:String? = when (Build.VERSION.SDK_INT) {
                    Build.VERSION_CODES.TIRAMISU -> {
                        ShizukuExec("service call package 131 s16 ${appInfo.appPkg} i32 0 i32 0".toByteArray())
                    }
                    Build.VERSION_CODES.S, Build.VERSION_CODES.S_V2 -> {
                        ShizukuExec("service call package 134 s16 ${appInfo.appPkg} i32 0 i32 0".toByteArray())
                    }
                    else -> {
                        ShizukuExec("pm uninstall --user 0 ${appInfo.appPkg}".toByteArray())
                    }
                }
                MaterialAlertDialogBuilder(context)
                    .setTitle("结果")
                    .setMessage(t)
                    .setPositiveButton("ok") { _, _ ->a.value=!a.value }
                    .show()
            }
            .setNegativeButton("取消") { _, _ -> }
            .show()
    }

    private fun reinstall(appInfo: AppInfo, a:MutableState<Boolean>) {
        MaterialAlertDialogBuilder(context)
            .setTitle("尝试重装")
            .setMessage("您将尝试重装 ${appInfo.appPkg} ,此操作仅系统自带核心app可用")
            .setPositiveButton("确定") { _, _ ->
                Toast.makeText(context, "请稍等...", Toast.LENGTH_LONG).show()
                val t:String? = when (Build.VERSION.SDK_INT) {
                    Build.VERSION_CODES.TIRAMISU -> {
                        ShizukuExec("service call package 131 s16 ${appInfo.appPkg} i32 1 i32 0".toByteArray())
                    }
                    Build.VERSION_CODES.S, Build.VERSION_CODES.S_V2 -> {
                        ShizukuExec("service call package 134 s16 ${appInfo.appPkg} i32 1 i32 0".toByteArray())
                    }
                    else -> {
                        ShizukuExec("pm install-existing ${appInfo.appPkg}".toByteArray())
                    }
                }
                MaterialAlertDialogBuilder(context)
                    .setTitle("结果")
                    .setMessage(t )
                    .setPositiveButton("ok") { _, _ ->
                        //重载页面
                        a.value=!a.value
                    }
                    .show()
            }
            .setNegativeButton("取消") { _, _ -> }
            .show()

    }
    private fun patchProcessLimit() {
        Toast.makeText(context, "请稍等...", Toast.LENGTH_LONG).show()
        ShizukuExec("device_config set_sync_disabled_for_tests persistent;device_config put activity_manager max_cached_processes 2147483647;device_config put activity_manager max_phantom_processes 2147483647".toByteArray())
        MaterialAlertDialogBuilder(context)
            .setTitle("关闭缓存进程和虚进程数量限制")
            .setMessage("调整完成，是否立即重启")
            .setPositiveButton("立即重启") { _, _ ->
                ShizukuExec("reboot".toByteArray())
            }
            .setNegativeButton("暂不重启"){_,_-> }
            .show()
    }
    private fun unpatchProcessLimit() {
        Toast.makeText(context, "请稍等...", Toast.LENGTH_LONG).show()
        ShizukuExec("device_config set_sync_disabled_for_tests none".toByteArray())
        MaterialAlertDialogBuilder(context)
            .setTitle("还原缓存进程和虚进程数量限制")
            .setMessage("还原完成，是否立即重启")
            .setPositiveButton("立即重启") { _, _ ->
                ShizukuExec("reboot".toByteArray())
            }
            .setNegativeButton("暂不重启"){_,_-> }
            .show()
    }
    private fun ShizukuExec(cmd: ByteArray): String? {
        if (br) {
            return "正在执行其他操作"
        }
        if (!b || !c) {
            Toast.makeText(context, "Shizuku 状态异常", Toast.LENGTH_SHORT).show()
            return "Shizuku 状态异常"
        }
        br = true

        val p: ShizukuRemoteProcess
        val op = arrayOfNulls<String>(1)
        try {
            OLog.i("运行shell", "开始运行$cmd")
            p = Shizuku.newProcess(arrayOf("sh"), null, null)
            val out: OutputStream = p.outputStream
            out.write(cmd)
            out.flush()
            out.close()
            h2 = Thread {
                try {
                    val outText = StringBuilder()
                    val reader = BufferedReader(InputStreamReader(p.inputStream))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        outText.append(line).append("\n")
                    }
                    reader.close()
                    val output = outText.toString()
                    OLog.i("运行shell", "Output_Normal:\n$output")
                    op[0] = output
                } catch (ignored: java.lang.Exception) {
                }
            }
            h2!!.start()
            h3 = Thread {
                try {
                    val outText = StringBuilder()
                    val reader = BufferedReader(InputStreamReader(p.getErrorStream()))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        outText.append(line).append("\n")
                    }
                    reader.close()
                    val output = outText.toString()
                    op[0] += output
                    OLog.i("运行shell", "Output_Error:\n$output")
                } catch (ignored: java.lang.Exception) {
                }
            }
            h3!!.start()

            p.waitFor()
            h2!!.join()
            ReturnValue = p.exitValue()
            OLog.i("运行shell", "跑完了")
            p.destroyForcibly()
            br = false

            return op[0]
        } catch (ignored: java.lang.Exception) {
        }
        return "null"
    }

    private fun showImageDialog(imageName: String) {
        val builder: AlertDialog.Builder = MaterialAlertDialogBuilder(this)

        // 创建一个 ImageView 并添加到对话框中
        val imageView = ImageView(this)
        try {
            val `is` = assets.open(imageName)
            val bitmap = BitmapFactory.decodeStream(`is`)
            imageView.setImageBitmap(bitmap)
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        } catch (e: IOException) {
            e.printStackTrace()
        }
        builder.setView(imageView) // 将 ImageView 加到对话框中
        builder.setNegativeButton("OK") { dialog, which ->
            // 点击 OK 按钮后的操作
            dialog.dismiss()
        }
        builder.show() // 显示对话框
    }


    private fun show_author() {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("coolmarket://u/3287595")
            startActivity(intent)
            finish()
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "打开酷安失败，已为您打开作者B站", Toast.LENGTH_SHORT).show()
            openLink("https://space.bilibili.com/329223542")
            // 处理ActivityNotFoundException异常，例如提示用户下载应用或打开其他应用商店
        }
    }


    private fun SetAppDisabled(
        isDisabled: MutableState<Boolean>,
        packagename: String,
        isExist: Boolean
    ) {
        if (isExist) {
            OShizuku.setAppDisabled(packagename, !isDisabled.value)
            val c = isAppDisabled(packagename)
            if (c != isDisabled.value) {
                isDisabled.value = c
            } else {
                Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "应用未安装", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission(): Boolean {
        return try {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (err: Throwable) {
            false
        }
    }

    private fun getAppIconByPackageName(packageName: String): Drawable? {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationIcon(applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    private fun getAppNameByPackageName(context: Context, packageName: String): String {
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

    private fun openLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    /****************
     *
     * 发起添加群流程。群号：IQOO⭐️交流群(262040855) 的 key 为： SqLJvDGqjKNDvc_O5dx6A164eLSo4QBG
     * 调用 joinQQGroup(SqLJvDGqjKNDvc_O5dx6A164eLSo4QBG) 即可发起手Q客户端申请加群 IQOO⭐️交流群(262040855)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     */
    private fun joinQQGroup(key: String): Boolean {
        val intent = Intent()
        intent.data =
            Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true
        } catch (e: java.lang.Exception) {
            // 未安装手Q或安装的版本不支持
            false
        }
    }

    private fun join_qq() {
        val is_join_succeed = joinQQGroup("SqLJvDGqjKNDvc_O5dx6A164eLSo4QBG")
        if (!is_join_succeed) {
            Toast.makeText(
                this,
                "未安装手Q或安装的版本不支持, 请手动加群262040855",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun isAppDisabled(appPackageName: String): Boolean {
        val packageManager: PackageManager = context.packageManager

        val packageInfo = packageManager.getPackageInfo(appPackageName, 0)
        // 应用被停用或者处于默认状态（未设置启用状态），返回 true；其他状态返回 false
        return !packageInfo.applicationInfo.enabled
    }

    private fun isInstalled(packageName: String): Boolean {
        val pm = context.packageManager
        try {
            val packageInfo = pm.getPackageInfo(packageName, 0)
            OLog.i("应用安装判断", "$packageName 已安装")
            return packageInfo != null
        } catch (ep: Throwable) {
            OLog.i("应用安装判断", "$packageName 未安装")
            OLog.e("应用安装判断报错", ep)
        }
        return false
    }




    @Composable
    fun AppListItem(appInfo: AppInfo) {
        //让 compose监听这个的变化
        val isDisabled = remember { mutableStateOf(appInfo.isDisabled) }
        val refreshing=remember { mutableStateOf(false) }
        var isMenuVisible by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                ,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var o=1
            if(refreshing.value||!refreshing.value) o=2
            if (isInstalled(appInfo.appPkg)) {
                appInfo.appName = getAppNameByPackageName(context, appInfo.appPkg)
                appInfo.isDisabled = isAppDisabled(appInfo.appPkg)
            } else {
                appInfo.isExist = false
                appInfo.appName = "未安装"
            }
            val appIcon = getAppIconByPackageName(appInfo.appPkg)
            if (appIcon != null) {
                Image(
                    painter = rememberDrawablePainter(appIcon),
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterVertically),
                    contentDescription = null
                )
            }
            // 左边显示应用名称
            Column(modifier = Modifier.weight(0.5f)) {
                Text(
                    text = appInfo.appName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (!appInfo.isExist) Color(0xFFFF6E40) else LocalContentColor.current
                )
                Text(text = appInfo.appPkg, style = MaterialTheme.typography.bodySmall)
            }

            // 中间显示禁用状态文本
            Text(
                text = if (!appInfo.isExist) "Unknown" else if (isDisabled.value) "Disable" else "Enable",
                color = if (!appInfo.isExist) Color(0xFFFF6E40)
                else if (isDisabled.value) Color(0xFFFF5252)
                else Color(0xFF59F0A6),
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
                Icon(
                    imageVector = icon,

                    contentDescription = if (!appInfo.isExist) "Unknown" else if (isDisabled.value) "Disable" else "Enable"
                )
            }
            IconButton(
                onClick = { isMenuVisible = true }
            ) {

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = refreshing.value.toString()
                )
                DropdownMenu(
                    expanded = isMenuVisible,
                    onDismissRequest = { isMenuVisible = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "uninstall"
                            )
                        },
                        text = { Text(text = "尝试卸载") }, onClick = { isMenuVisible=false;uninstall(appInfo,refreshing) }
                        // 处理菜单项点击事件，这里可以添加卸载逻辑
                    )
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "uninstall"
                            )
                        }, text = { Text(text = "尝试重装") }, onClick = {
                            // ...
                            isMenuVisible=false
                            reinstall(appInfo,refreshing)
                        })
                }
            }

        }

    }


    @Composable
    fun AppList(appList: List<AppInfo>) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(optlist + appList) { appInfo ->
                AppListItem(
                    appInfo = appInfo
                )
            }
        }

    }

    private fun showLicenses() {
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

    @Composable
    fun ItemsCardWidget(
        colors: CardColors = CardDefaults.elevatedCardColors(),
        onClick: (() -> Unit)? = null,
        showItemIcon: Boolean = false,
        icon: (@Composable () -> Unit)? = null,
        title: (@Composable () -> Unit)? = null,
        items: List<OriginCardItem>,
        buttons: (@Composable () -> Unit)? = null
    ) {
        CardWidget(
            colors = colors,
            onClick = onClick,
            icon = icon,
            title = title,
            content = {
                @Composable
                fun ItemWidget(item: OriginCardItem) {
                    Row(
                        modifier = Modifier
                            .clickable(enabled = item.onClick != null, onClick = item.onClick ?: {})
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        if (showItemIcon) {
                            if (item.icon != null) {
                                Icon(imageVector = item.icon, contentDescription = item.label)
                            } else {
                                Spacer(modifier = Modifier.size(32.dp))
                            }
                        }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Text(text = item.label, style = MaterialTheme.typography.bodyLarge)
                            if (item.content != null) {
                                Text(
                                    text = item.content,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                Column {
                    items.forEach {
                        ItemWidget(it)
                    }
                }
            },
            buttons = buttons
        )
    }

    @Composable
    fun CardWidget(
        colors: CardColors = CardDefaults.elevatedCardColors(),
        onClick: (() -> Unit)? = null,
        icon: (@Composable () -> Unit)? = null,
        title: (@Composable () -> Unit)? = null,
        content: (@Composable () -> Unit)? = null,
        buttons: (@Composable () -> Unit)? = null
    ) {
        ElevatedCard(
            colors = colors,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = onClick != null, onClick = onClick ?: {})
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (icon != null) {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.secondary) {
                        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            icon()
                        }
                    }
                }
                if (title != null) {
                    ProvideTextStyle(value = MaterialTheme.typography.titleLarge) {
                        Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            title()
                        }
                    }
                }
                if (content != null) {
                    Box {
                        content()
                    }
                }
                if (buttons != null) {
                    Box {
                        buttons()
                    }
                }
            }
        }
    }

    @Composable
    fun StatusWidget() {
        val containerColor = MaterialTheme.colorScheme.primaryContainer

        val onContainerColor = MaterialTheme.colorScheme.onPrimaryContainer

        val level = "Release"

        CardWidget(
            colors = CardDefaults.elevatedCardColors(
                containerColor = containerColor,
                contentColor = onContainerColor
            ),
            icon = {
                Image(
                    modifier = Modifier
                        .size(56.dp),
                    painter = rememberDrawablePainter(
                        drawable = ContextCompat.getDrawable(
                            LocalContext.current,
                            R.mipmap.ic_launcher
                        )
                    ),
                    contentDescription = stringResource(id = R.string.app_name)
                )
            },
            title = {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            content = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "$level [${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})]",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        )
    }

    @Composable
    fun DonateWidget() {
        LocalContext.current

        val items = listOf(
            OriginCardItem(
                icon = ImageVector.Companion.vectorResource(R.drawable.ic_alipay),
                label = "支付宝",
                onClick = {
                    showImageDialog("zfb.jpg")
                }
            ),
            OriginCardItem(
                icon = ImageVector.Companion.vectorResource(R.drawable.ic_wechatpay),
                label = "微信",
                onClick = {
                    showImageDialog("wx.png")
                }
            ),

            )
        ItemsCardWidget(
            title = {
                Text(text = "捐赠")
            },
            items = items,
            showItemIcon = true
        )
    }

    @Composable
    fun DiscussWidget() {
        val items = listOf(
            OriginCardItem(
                icon = ImageVector.Companion.vectorResource(R.drawable.ic_bilibili),
                label = "BiliBili（开发者）",
                onClick = {
                    openLink("https://space.bilibili.com/329223542")
                }),
            OriginCardItem(
                icon = ImageVector.Companion.vectorResource(R.drawable.ic_bilibili),
                label = "BiliBili（合作伙伴）",
                onClick = {
                    openLink("https://space.bilibili.com/1289434708")
                }
            ),
            OriginCardItem(
                icon = ImageVector.Companion.vectorResource(R.drawable.ic_outline_coolapk),
                label = "酷安（开发者）",
                onClick = {
                    show_author()
                }
            ),
            OriginCardItem(
                icon = ImageVector.Companion.vectorResource(R.drawable.ic_outline_qq),
                label = "QQ群",
                onClick = {
                    join_qq()
                }
            ),

            )
        ItemsCardWidget(
            title = {
                Text(text = "讨论&反馈&联系我们")
            },
            items = items,
            showItemIcon = true
        )
    }

    @Composable
    fun OpenSourceWidget() {
        val items = listOf(
            OriginCardItem(
                icon = ImageVector.Companion.vectorResource(R.drawable.ic_outline_code),

                label = "Github",
                onClick = {
                    openLink("https://github.com/ItosEO/OriginPlan")
                }
            ),
            OriginCardItem(
                icon = ImageVector.Companion.vectorResource(R.drawable.ic_outline_lisence),

                label = "许可证",
                onClick = {
                    showLicenses()
                }
            ),

            )
        ItemsCardWidget(
            title = {
                Text(text = "开源")
            },
            items = items,
            showItemIcon = true
        )
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Opt() {
        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(text = "优化")
                },
            )
        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // 将子项垂直居中
            ) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 45.dp)
                ) {
                    FilledTonalButton(
                        modifier = Modifier
                            .size(width = 130.dp, height = 60.dp),
                        shape = RoundedCornerShape(30),
                        onClick = { opt_setappstauts(false) }
                    ) {
                        Text("一键优化")
                    }
                    Spacer(modifier = Modifier.width(25.dp))
                    FilledTonalButton(
                        modifier = Modifier
                            .size(width = 130.dp, height = 60.dp),
                        shape = RoundedCornerShape(30),
                        onClick = { opt_setappstauts(true) }
                    ) {
                        Text("还原")
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(vertical = 45.dp)
                ) {
                    FilledTonalButton(
                        modifier = Modifier
                            .size(width = 130.dp, height = 60.dp),
                        shape = RoundedCornerShape(30),
                        onClick = {
                            patchProcessLimit()
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
                            unpatchProcessLimit()
                        }
                    ) {
                        Text("还原\nAndroid进程设置", textAlign = TextAlign.Center)
                    }
                }
            }

        }


    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun About() {
        Scaffold(
            modifier = Modifier
                .windowInsetsPadding(windowInsets)
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "关于")
                    },
                )
            },
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    StatusWidget()
                }
                item {
                    DonateWidget()
                }
                item {
                    DiscussWidget()
                }
                item {
                    OpenSourceWidget()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Details() {

        Column {
            val recompose = currentRecomposeScope
            //val appList = remember { generateAppList(context) }
            // TopAppBar
            TopAppBar(title = { Text(text = "原·初") },actions={
                IconButton(onClick = { recompose.invalidate() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "refresh"
                    )
                }
            })
            // AppList
            AppList(appList = pkglist)
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppListScreen(context: Context) {
        val navController = rememberNavController()
        val isLandscapeScreen =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        Scaffold(
            //设置底部导航栏
            bottomBar = {
                if (!isLandscapeScreen) {
                    NavigationBar {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        NavigationBarItem(

                            icon = {
                                when (currentDestination?.route) {
                                    "1" -> {
                                        // 选中时的图标
                                        Icon(Icons.Filled.Settings, contentDescription = null)
                                    }

                                    else -> {
                                        // 未选中时的图标
                                        Icon(Icons.Outlined.Settings, contentDescription = null)
                                    }
                                }
                            },
                            label = {
                                Text(
                                    text = "Optimization"
//                                modifier = Modifier.alpha(if (currentDestination?.route == "Details") 1f else 0f)
                                )
                            },
                            selected = currentDestination?.route == "1",
                            alwaysShowLabel = false,
                            onClick = {
                                navController.navigate("1") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = {
                                when (currentDestination?.route) {
                                    "2" -> {
                                        // 选中时的图标
                                        Icon(Icons.Filled.Create, contentDescription = null)
                                    }

                                    else -> {
                                        // 未选中时的图标
                                        Icon(Icons.Outlined.Create, contentDescription = null)
                                    }
                                }
                            },
                            label = {
                                Text(
                                    text = "Details",
//                                modifier = Modifier.alpha(if (currentDestination?.route == "Details") 1f else 0f)
                                )
                            },
                            selected = currentDestination?.route == "2",
                            alwaysShowLabel = false,
                            onClick = {
                                navController.navigate("2") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = {
                                when (currentDestination?.route) {
                                    "3" -> {
                                        // 选中时的图标
                                        Icon(Icons.Filled.Info, contentDescription = null)
                                    }

                                    else -> {
                                        // 未选中时的图标
                                        Icon(Icons.Outlined.Info, contentDescription = null)
                                    }
                                }
                            },
                            label = {
                                Text(
                                    text = "About",
//                                modifier = Modifier.alpha(if (currentDestination?.route == "About") 1f else 0f)
                                )
                            },
                            selected = currentDestination?.route == "3",
                            alwaysShowLabel = false,
                            onClick = {
                                navController.navigate("3") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }

                } else {
                    NavigationRail {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                        )
                        NavigationRailItem(

                            icon = {
                                when (currentDestination?.route) {
                                    "1" -> {
                                        // 选中时的图标
                                        Icon(Icons.Filled.Settings, contentDescription = null)
                                    }

                                    else -> {
                                        // 未选中时的图标
                                        Icon(Icons.Outlined.Settings, contentDescription = null)
                                    }
                                }
                            },
                            label = {
                                Text(
                                    text = "Optimization"
//                                modifier = Modifier.alpha(if (currentDestination?.route == "Details") 1f else 0f)
                                )
                            },
                            selected = currentDestination?.route == "1",
                            alwaysShowLabel = false,
                            onClick = {
                                navController.navigate("1") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationRailItem(
                            icon = {
                                when (currentDestination?.route) {
                                    "2" -> {
                                        // 选中时的图标
                                        Icon(Icons.Filled.Create, contentDescription = null)
                                    }

                                    else -> {
                                        // 未选中时的图标
                                        Icon(Icons.Outlined.Create, contentDescription = null)
                                    }
                                }
                            },
                            label = {
                                Text(
                                    text = "Details",
//                                modifier = Modifier.alpha(if (currentDestination?.route == "Details") 1f else 0f)
                                )
                            },
                            selected = currentDestination?.route == "2",
                            alwaysShowLabel = false,
                            onClick = {
                                navController.navigate("2") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationRailItem(
                            icon = {
                                when (currentDestination?.route) {
                                    "3" -> {
                                        // 选中时的图标
                                        Icon(Icons.Filled.Info, contentDescription = null)
                                    }

                                    else -> {
                                        // 未选中时的图标
                                        Icon(Icons.Outlined.Info, contentDescription = null)
                                    }
                                }
                            },
                            label = {
                                Text(
                                    text = "About",
//                                modifier = Modifier.alpha(if (currentDestination?.route == "About") 1f else 0f)
                                )
                            },
                            selected = currentDestination?.route == "3",
                            alwaysShowLabel = false,
                            onClick = {
                                navController.navigate("3") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }

        ) {
            if (isLandscapeScreen) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 72.dp)
                ) {
                    // 在这里放置你的内容
                    NavHost(
                        navController = navController,
                        startDestination = "2"
                    ) {
                        composable("2") { Details() }
                        composable("3") { About() }
                        composable("1") { Opt() }
                        // 添加其他页面的 composable 函数，类似上面的示例
                    }
                }
            } else {

                NavHost(
                    navController = navController,
                    startDestination = "2"
                ) {
                    composable("2") { Details() }
                    composable("3") { About() }
                    composable("1") { Opt() }
                    // 添加其他页面的 composable 函数，类似上面的示例
                }
            }
        }

    }


    @Composable
    fun AppListContent() {
        AppListScreen(LocalContext.current)
    }

    fun generateAppList(context: Context): List<AppInfo> {
        var a: Boolean
        // 这里添加你的应用信息
        for (appinfo in pkglist) {
            if (isInstalled(appinfo.appPkg)) {
                appinfo.appName = getAppNameByPackageName(context, appinfo.appPkg)
                a = isAppDisabled(appinfo.appPkg)
                appinfo.isDisabled = a
            } else {
                appinfo.isExist = false
                appinfo.appName = "未安装"
            }
        }
        for (appinfo in optlist) {
            if (isInstalled(appinfo.appPkg)) {
                appinfo.appName = getAppNameByPackageName(context, appinfo.appPkg)
                a = isAppDisabled(appinfo.appPkg)
                appinfo.isDisabled = a
            } else {
                appinfo.isExist = false
                appinfo.appName = "未安装"
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
    @OptIn(ExperimentalMaterial3Api::class)
    @Preview(showBackground = true)
    @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
    @Composable
    fun GreetingPreview() {
        OriginPlanTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    val recompose = currentRecomposeScope
                    // TopAppBar
                    TopAppBar(title = { Text(text = "原·初") },actions={
                        IconButton(onClick = { recompose.invalidate() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "refresh"
                            )
                        }
                    })
                }
            }
        }
    }
}




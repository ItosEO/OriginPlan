package com.itos.xplan

import AboutPage
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.RecomposeScope
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alibaba.fastjson.JSONObject
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.itos.xplan.datatype.AppInfo
import com.itos.xplan.datatype.ConfigData
import com.itos.xplan.ui.Pages.OptPage
import com.itos.xplan.ui.theme.OriginPlanTheme
import com.itos.xplan.utils.NetUtils
import com.itos.xplan.utils.OData
import com.itos.xplan.utils.OLog
import com.itos.xplan.utils.OPackage
import com.itos.xplan.utils.OPackage.getAppIconByPackageName
import com.itos.xplan.utils.OShizuku
import com.itos.xplan.utils.OShizuku.checkShizuku
import com.itos.xplan.utils.OUI
import com.itos.xplan.utils.SpUtils
import com.itos.xplan.utils.SystemPropertiesProxy
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnBinderReceivedListener
import rikka.shizuku.Shizuku.UserServiceArgs
import rikka.shizuku.ShizukuRemoteProcess
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.regex.Matcher
import java.util.regex.Pattern


// TODO 拆Details页面

class XPlan : AppCompatActivity() {
    val context: Context = this
    var ReturnValue = 0
    var isRunning = false
    var h2: Thread? = null
    var h3: Thread? = null
    var isShizukuStart = true
    var isShizukuAuthorized = false
    private var iUserService: IUserService? = null
    var show_notice: String = "暂无公告"

    private val pkglist = mutableListOf<AppInfo>()
    val optlist = mutableListOf<AppInfo>()

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
        load_applist()
        app = this
        // 3是a13，2是a12（service call），1是pm增强，0是pm
        when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.TIRAMISU -> {
                SpUtils.setParam(context, "method", 3)
            }

            Build.VERSION_CODES.S, Build.VERSION_CODES.S_V2 -> {
                SpUtils.setParam(context, "method", 2)
            }

            else -> {
                SpUtils.setParam(context, "method", 1)
            }
        }
//        OUI.check_secure_premission
        init_shizuku()
        guide()
        generateAppList(context)
        registerUser()
        update_notice()
//        Shizuku.bindUserService(userServiceArgs, serviceConnection)
//        update_config()
    }



    private fun load_applist() {
        lifecycleScope.launch(Dispatchers.IO) {
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
            } catch (_: Exception) {
            }
        }

    }

    private fun guide() {
        if (SpUtils.getParam(context, "if_first_time", true) as Boolean) {
            MaterialAlertDialogBuilder(context)
                .setTitle("帮助")
                .setMessage("您需要Shiuzku激活教程吗")
                .setPositiveButton("好的") { dialog, which ->
                    SpUtils.setParam(context, "if_first_time", false)
                    OUI.openLink("https://www.bilibili.com/video/BV1o94y1u7Kq")
                }
                .setNegativeButton("我会") { dialog, which ->
                    SpUtils.setParam(context, "if_first_time", false)
                    dialog.dismiss()
                }
                .show()
                .setCancelable(false)
        }
    }

    private fun update_notice() {
        val handler = CoroutineExceptionHandler { _, exception ->
            // 在这里处理异常，例如打印日志、上报异常等
            OLog.e("Update Notice Exception:", exception)
            MaterialAlertDialogBuilder(context)
                .setTitle("错误")
                .setMessage("获取云端更新失败\n请检查网络连接")
                .setPositiveButton("了解", null)
                .show()
        }
        lifecycleScope.launch(Dispatchers.IO + handler) {
            // 后台工作
            val update =
                NetUtils.Get("https://itos.codegang.top/share/XPlan/OriginOS/app_update.json")
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
                show_notice = notice
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
                            OUI.openLink(url)
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

    private fun update_config() {
        val handler = CoroutineExceptionHandler { _, exception ->
            // 在这里处理异常，例如打印日志、上报异常等
            OLog.e("Update Config Exception:", exception)
        }

        lifecycleScope.launch(Dispatchers.IO + handler) {
            // 后台工作
            val config =
                NetUtils.Get("https://itos.codegang.top/share/XPlan/OriginOS/app_config.json")

            // 切换到主线程进行 UI 操作
            withContext(Dispatchers.Main) {
                // UI 操作，例如显示 Toast
                OData.configdata = JSONObject.parseObject(config, ConfigData::class.java)
                OLog.i(
                    "系统参数调优配置:",
                    config + "\n" + OData.configdata.toString()
                )
            }
        }
    }

    private fun init_shizuku() {
        Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
        checkShizuku()
        Shizuku.addBinderReceivedListenerSticky(BINDER_RECEVIED_LISTENER)
        Shizuku.addBinderDeadListener(BINDER_DEAD_LISTENER)
        Shizuku.bindUserService(userServiceArgs, serviceConnection)
    }
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
//            Toast.makeText(app, "Shizuku服务连接成功", Toast.LENGTH_SHORT).show()
            if (iBinder.pingBinder()) {
                iUserService = IUserService.Stub.asInterface(iBinder)
            }
        }
        override fun onServiceDisconnected(componentName: ComponentName) {
            Toast.makeText(app, "Shizuku服务连接断开", Toast.LENGTH_SHORT).show()
            iUserService = null
        }
    }
    private val userServiceArgs = UserServiceArgs(
        ComponentName(
            BuildConfig.APPLICATION_ID,
            UserService::class.java.getName()
        )
    )
        .daemon(false)
        .processNameSuffix("adb_service")
        .debuggable(BuildConfig.DEBUG)
        .version(BuildConfig.VERSION_CODE)


    private fun onRequestPermissionsResult() {
        checkShizuku()
        Shizuku.bindUserService(userServiceArgs, serviceConnection)
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeBinderReceivedListener(BINDER_RECEVIED_LISTENER)
        Shizuku.removeBinderDeadListener(BINDER_DEAD_LISTENER)
        Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
        Shizuku.unbindUserService(userServiceArgs, serviceConnection, true);

    }

    private fun registerUser() {
        checkShizuku()
        var os = ""
        var deviceId = ""
        var device_model = ""
        Log.d("登记用户", "$isShizukuStart $isShizukuAuthorized")
        try {
//                os = ShizukuExec_US("getprop ro.vivo.os.build.display.id".getBytes()).trim();
//                device_model = ShizukuExec_US("getprop ro.vivo.internet.name".getBytes()).trim();
//                system_version = ShizukuExec_US("getprop ro.vivo.os.build.display.id".getBytes()).trim();
//                deviceId = ShizukuExec_US("getprop ro.serialno".getBytes()).trim() + "_" + android.os.Build.DEVICE;
            os = SystemPropertiesProxy.get(context, "ro.vivo.os.build.display.id").trim()
            device_model = SystemPropertiesProxy.get(context, "ro.vivo.internet.name").trim()
            deviceId = SystemPropertiesProxy.get(context, "ro.serialno") + "_" + Build.DEVICE
            if (deviceId == "_" + Build.DEVICE) {
                val sn = ShizukuExec_US("getprop ro.serialno")?.trim()
                if (sn == "Shizuku 状态异常") {
                    Log.d("登记用户-error", "Shizuku 状态异常")
                    return
                }
                deviceId = sn + "_" + Build.DEVICE;
            }
            Log.d("登记用户", "$os $device_model $deviceId")
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "[反射异常]", Toast.LENGTH_SHORT).show()
            Log.d("登记用户-error", e.toString())
        }
        val finalDeviceId = deviceId
        val finalDevice_model = device_model
        val finalOs = os
        Log.d("登记用户", "$finalOs $finalDevice_model")
        lifecycleScope.launch(Dispatchers.IO) {
            // 后台工作
            Log.d(
                "登记用户-请求url",
                "http://cloud.itos.codegang.top:44553/xplan/originos/reg_user/?deviceid=$deviceId&device_model=$device_model&os=$os&soc=${Build.BOARD}&t=" + System.currentTimeMillis() / 1000
            )
            val temp: String =
                NetUtils.Get("http://cloud.itos.codegang.top:44553/xplan/originos/reg_user/?deviceid=$deviceId&device_model=$device_model&os=$os&soc=${Build.BOARD}&t=" + System.currentTimeMillis() / 1000)
            Log.d("登记用户-返回", temp)
        }

    }

    private fun uninstall(appInfo: AppInfo, a: RecomposeScope) {

        MaterialAlertDialogBuilder(context)
            .setTitle("尝试卸载")
            .setMessage("您将卸载 ${appInfo.appName}(${appInfo.appPkg})")
            .setPositiveButton("确定") { _, _ ->
                val t: String? = when (SpUtils.getParam(context, "method", 1)) {
                    3 -> {
                        ShizukuExec_US("service call package 131 s16 ${appInfo.appPkg} i32 0 i32 0")
                    }

                    2 -> {
                        ShizukuExec_US("service call package 134 s16 ${appInfo.appPkg} i32 0 i32 0")
                    }

                    1 -> {
                        ShizukuExec_US("pm uninstall --user 0 ${appInfo.appPkg}")
                    }

                    else -> {
                        ShizukuExec_US("pm uninstall ${appInfo.appPkg}")
                    }
                }
                MaterialAlertDialogBuilder(context)
                    .setTitle("结果")
                    .setMessage(t)
                    .setPositiveButton("ok") { _, _ ->
                        appInfo.isExist =
                            OPackage.isInstalled(appInfo.appPkg, context.packageManager)
                        appInfo.appName = getAppNameByPackageName(context, appInfo.appPkg)
                        appInfo.appIcon =
                            getAppIconByPackageName(appInfo.appPkg, context.packageManager)
                        a.invalidate()
                    }
                    .show()
            }
            .setNegativeButton("取消") { _, _ -> }
            .show()
    }

    private fun reinstall(appInfo: AppInfo, a: RecomposeScope) {
        MaterialAlertDialogBuilder(context)
            .setTitle("尝试重装")
            .setMessage("您将尝试重装 ${appInfo.appPkg} ,此操作仅系统自带核心app可用")
            .setPositiveButton("确定") { _, _ ->
                Toast.makeText(context, "请稍等...", Toast.LENGTH_LONG).show()
                val t: String? = when (SpUtils.getParam(context, "method", 1)) {
                    3 -> {
                        ShizukuExec_US("service call package 131 s16 ${appInfo.appPkg} i32 1 i32 0")
                    }

                    2 -> {
                        ShizukuExec_US("service call package 134 s16 ${appInfo.appPkg} i32 1 i32 0")
                    }

                    else -> {
                        ShizukuExec_US("pm install-existing ${appInfo.appPkg}")
                    }
                }
                MaterialAlertDialogBuilder(context)
                    .setTitle("结果")
                    .setMessage(t)
                    .setPositiveButton("ok") { _, _ ->
                        //重载页面
                        appInfo.isExist =
                            OPackage.isInstalled(appInfo.appPkg, context.packageManager)
                        if (appInfo.isExist) {
                            appInfo.isDisabled = isAppDisabled(appInfo.appPkg)
                            appInfo.appName = getAppNameByPackageName(context, appInfo.appPkg)
                            appInfo.appIcon =
                                getAppIconByPackageName(appInfo.appPkg, context.packageManager)
                            a.invalidate()
                        }
                    }
                    .show()
            }
            .setNegativeButton("取消") { _, _ -> }
            .show()

    }

    fun patchProcessLimit() {
        Toast.makeText(context, "请稍等...", Toast.LENGTH_LONG).show()
        ShizukuExec_US("device_config set_sync_disabled_for_tests persistent;device_config put activity_manager max_cached_processes 2147483647;device_config put activity_manager max_phantom_processes 2147483647;echo success")
        MaterialAlertDialogBuilder(context)
            .setTitle("关闭缓存进程和虚进程数量限制")
            .setMessage("调整完成，是否立即重启")
            .setPositiveButton("立即重启") { _, _ ->
                ShizukuExec_US("reboot")
            }
            .setNegativeButton("暂不重启") { _, _ -> }
            .show()
    }

    fun unpatchProcessLimit() {
        Toast.makeText(context, "请稍等...", Toast.LENGTH_LONG).show()
        ShizukuExec_US("device_config set_sync_disabled_for_tests none;device_config put activity_manager max_cached_processes 32;device_config put activity_manager max_phantom_processes 32")
        MaterialAlertDialogBuilder(context)
            .setTitle("还原缓存进程和虚进程数量限制")
            .setMessage("还原完成，是否立即重启")
            .setPositiveButton("立即重启") { _, _ ->
                ShizukuExec_US("reboot")
            }
            .setNegativeButton("暂不重启") { _, _ -> }
            .show()
    }

    fun ShizukuExec(cmd: ByteArray): String? {
        if (isRunning) {
            return "正在执行其他操作"
        }
        if (!isShizukuStart || !isShizukuAuthorized) {
            Toast.makeText(context, "Shizuku 状态异常", Toast.LENGTH_SHORT).show()
            return "Shizuku 状态异常"
        }
        isRunning = true

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
            isRunning = false

            return op[0]
        } catch (ignored: java.lang.Exception) {
        }
        return "null"
    }

    fun ShizukuExec_US(cmd: String): String? {
        if (isRunning) {
            return "正在执行其他操作"
        }
        if (!isShizukuStart || !isShizukuAuthorized) {
            Toast.makeText(context, "Shizuku 状态异常", Toast.LENGTH_SHORT).show()
            return "Shizuku 状态异常"
        }
        isRunning = true

        // 检查是否存在包含任意内容的双引号

        val pattern: Pattern = Pattern.compile("\"([^\"]*)\"")
        val matcher: Matcher = pattern.matcher(cmd)

        val list = ArrayList<String>()
        val pattern2: Pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)")
        val matcher2: Matcher = pattern2.matcher(cmd)
        while (matcher2.find()) {
            if (matcher2.group(1) != null) {
                // 如果是引号包裹的内容，取group(1)
                matcher2.group(1)?.let { list.add(it) }
            } else {
                // 否则取group(2)，即普通的单词
                matcher2.group(2)?.let { list.add(it) }
            }
        }

        // 这种方法可用于执行路径中带空格的命令，例如 ls /storage/0/emulated/temp dir/
        // 当然也可以执行不带空格的命令，实际上是要强于另一种执行方式的
        val rt = iUserService?.exec(cmd)
//        val rt = iUserService?.execArr(list.toTypedArray())
        isRunning = false
        return rt
        // 下面展示了两种不同的命令执行方法
//        return if (matcher.find()) {
//            val list = ArrayList<String>()
//            val pattern2: Pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)")
//            val matcher2: Matcher = pattern2.matcher(cmd)
//            while (matcher2.find()) {
//                if (matcher2.group(1) != null) {
//                    // 如果是引号包裹的内容，取group(1)
//                    matcher2.group(1)?.let { list.add(it) }
//                } else {
//                    // 否则取group(2)，即普通的单词
//                    matcher2.group(2)?.let { list.add(it) }
//                }
//            }
//
//            // 这种方法可用于执行路径中带空格的命令，例如 ls /storage/0/emulated/temp dir/
//            // 当然也可以执行不带空格的命令，实际上是要强于另一种执行方式的
//            iUserService.execArr(list.toTypedArray())
//        } else {
//            // 这种方法仅用于执行路径中不包含空格的命令，例如 ls /storage/0/emulated/
//            iUserService.execLine(cmd)
//        }
    }

    fun SetAppDisabled(
        isDisabled: MutableState<Boolean>,
        packagename: String,
        isExist: Boolean,
        isShowToast: Boolean = true,
        appinfolist: MutableState<AppInfo>? = null
    ): Boolean? {
        if (isExist) {
            OShizuku.setAppDisabled(packagename, !isDisabled.value)
            val c = isAppDisabled(packagename)
            if (c != isDisabled.value) {
                if (appinfolist != null) appinfolist.value.isDisabled = c
                isDisabled.value = c
                return true
            } else {
                if (isShowToast) {
                    Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show()
                }
                return false
            }
        } else {
            Toast.makeText(this, "应用未安装", Toast.LENGTH_SHORT).show()
            return null
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
        } ?: "未安装"
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

    fun isAppDisabled(appPackageName: String): Boolean {
        val packageManager: PackageManager = context.packageManager

        val packageInfo = packageManager.getPackageInfo(appPackageName, 0)
        // 应用被停用或者处于默认状态（未设置启用状态），返回 true；其他状态返回 false
        return !packageInfo.applicationInfo.enabled
    }


    @Composable
    fun AppListItem(appinfo: AppInfo) {
        //让 compose监听这个的变化
        var appInfo = remember { mutableStateOf(appinfo) }
        val isDisabled = remember { mutableStateOf(appInfo.value.isDisabled) }
//        val appinfo_remember=remember{ mutableStateOf(appInfo) }
//        val refreshing = remember { mutableStateOf(false) }
        var isMenuVisible by remember { mutableStateOf(false) }
        val recompose = currentRecomposeScope

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            OLog.i("重绘", "触发重绘")

            if (appInfo.value.appIcon != null) {
                Image(
                    painter = rememberDrawablePainter(appInfo.value.appIcon),
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.CenterVertically),
                    contentDescription = null
                )
            }
            // 左边显示应用名称
            Column(modifier = Modifier.weight(0.5f)) {
                Text(
                    text = appInfo.value.appName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (!appInfo.value.isExist) Color(0xFFFF6E40) else LocalContentColor.current
                )
                Text(text = appInfo.value.appPkg, style = MaterialTheme.typography.bodySmall)
            }

            // 中间显示禁用状态文本
            Text(
                text = if (!appInfo.value.isExist) "Unknown" else if (isDisabled.value) "Disable" else "Enable",
                color = if (!appInfo.value.isExist) Color(0xFFFF6E40)
                else if (isDisabled.value) Color(0xFFFF5252)
                else Color(0xFF59F0A6),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 16.dp)
            )
            // 右边是一个按钮
            IconButton(
                onClick = {
                    SetAppDisabled(
                        isDisabled,
                        appInfo.value.appPkg,
                        appInfo.value.isExist,
                        true,
                        appInfo
                    )
                }
            ) {

                val icon: ImageVector = if (appInfo.value.isExist && isDisabled.value) {
                    Icons.Default.Check
                } else if (appInfo.value.isExist) {
                    Icons.Default.Close
                } else {
                    Icons.Default.Warning
                }
                Icon(
                    imageVector = icon,

                    contentDescription = if (!appInfo.value.isExist) "Unknown" else if (isDisabled.value) "Disable" else "Enable"
                )
            }
            IconButton(
                onClick = { isMenuVisible = true }
            ) {

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
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
                        text = { Text(text = "尝试卸载") },
                        onClick = {
                            isMenuVisible = false;uninstall(
                            appInfo.value,
                            recompose
                        )
                        }
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
                            isMenuVisible = false
                            reinstall(appInfo.value, recompose)
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
                AppListItem(appInfo)
            }
        }

    }

    private fun copyText(text: String) = getSystemService<ClipboardManager>()
        ?.setPrimaryClip(ClipData.newPlainText(getString(R.string.app_name), text))

    private suspend fun onTerminalResult(exitValue: Int, msg: String?) =
        withContext(Dispatchers.Main) {
            if (exitValue == 0 && msg.isNullOrBlank()) return@withContext
            MaterialAlertDialogBuilder(context).apply {
                if (!msg.isNullOrBlank()) {
                    if (exitValue != 0) {
                        setTitle(getString(R.string.operation_failed, exitValue.toString()))
                    } else {
                        setTitle("结果")
                    }
                    setMessage(msg)
                    setNeutralButton(android.R.string.copy) { _, _ -> copyText(msg) }
                } else if (exitValue != 0) {
                    setMessage(getString(R.string.operation_failed, exitValue.toString()))
                }
            }.setPositiveButton(android.R.string.ok, null).show()
                .findViewById<MaterialTextView>(android.R.id.message)
                ?.setTextIsSelectable(true)
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Details() {
        generateAppList(context)

        Column {
            //val appList = remember { generateAppList(context) }
            // TopAppBar
            TopAppBar(title = { Text(text = "原·初") },
                actions = {
                    IconButton(onClick = {
                        if (!isShizukuAuthorized || !isShizukuStart) return@IconButton
                        val inputEditText = EditText(context).apply {
                            hint = "Terminal"
                            inputType =
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                            setSingleLine(false)
                            // 可选：设置最小行数以显示更多的输入区域
                            minLines = 2
                            // 可选：设置最大行数（如果需要）
                            maxLines = 5
                        }


                        MaterialAlertDialogBuilder(context)
                            .setTitle("终端")
                            .setView(inputEditText)
                            .setPositiveButton(android.R.string.ok) { _, _ ->
                                lifecycleScope.launch {
                                    val result = ShizukuExec_US(inputEditText.text.toString())
                                    OLog.i("终端结果：", result!!)
                                    onTerminalResult(ReturnValue, result)
                                }
                            }
                            .setNegativeButton(android.R.string.cancel, null)
                            .show()

                    }) {
                        Icon(
                            imageVector = ImageVector.Companion.vectorResource(R.drawable.ic_baseline_terminal),
                            contentDescription = "terminal"
                        )
                    }
                    IconButton(
                        onClick = {
                            val options = arrayOf(
                                "pm命令",
                                "pm命令（增强）",
                                "service call（Android 12）",
                                "service call（Android 13）"
                            )
                            var selectedItem = SpUtils.getParam(context, "method", 1) as Int
                            Toast.makeText(
                                context,
                                "设置卸载、重装操作的实现方案\nservice call一般可以卸载更多app",
                                Toast.LENGTH_SHORT
                            ).show()
                            MaterialAlertDialogBuilder(context)
                                .setTitle("设置方案")
                                .setSingleChoiceItems(options, selectedItem) { _, which ->
                                    // 设置选中状态
                                    selectedItem = which
                                }
                                .setPositiveButton("确定") { _, _ ->
                                    // 处理确定按钮点击事件，可以根据 selectedItem 执行相应逻辑
                                    if (selectedItem != -1) {
                                        when (selectedItem) {
                                            0 -> {
                                                // 选择了 "pm命令"
                                                SpUtils.setParam(context, "method", 0)
                                            }

                                            1 -> {
                                                // 选择了 "pm命令"
                                                SpUtils.setParam(context, "method", 1)
                                            }

                                            2 -> {
                                                // 选择了 "service call（Android 12）"
                                                SpUtils.setParam(context, "method", 2)
                                            }

                                            3 -> {
                                                // 选择了 "service call（Android 13）"
                                                SpUtils.setParam(context, "method", 3)
                                            }
                                        }
                                    }
                                }
                                .setNegativeButton("取消") { dialog, _ ->
                                    // 处理取消按钮点击事件
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "settings"
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


            }

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = it.calculateBottomPadding()) // 添加 padding,防止遮挡内容
            ) {

                NavHost(
                    navController = navController,
                    startDestination = "1"
                ) {
                    OLog.i("界面", "绘制横屏开始")
                    composable("2") { Details() }
                    composable("3") { AboutPage() }
                    composable("1") { OptPage() }
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
            if (OPackage.isInstalled(appinfo.appPkg, context.packageManager)) {
                appinfo.appName = getAppNameByPackageName(context, appinfo.appPkg)
                a = isAppDisabled(appinfo.appPkg)
                appinfo.isDisabled = a
            } else {
                appinfo.isExist = false
                appinfo.appName = "未安装"
            }
            appinfo.appIcon = getAppIconByPackageName(appinfo.appPkg, context.packageManager)
        }
        for (appinfo in optlist) {
            if (OPackage.isInstalled(appinfo.appPkg, context.packageManager)) {
                appinfo.appName = getAppNameByPackageName(context, appinfo.appPkg)
                a = isAppDisabled(appinfo.appPkg)
                appinfo.isDisabled = a
            } else {
                appinfo.isExist = false
                appinfo.appName = "未安装"
            }
            appinfo.appIcon = getAppIconByPackageName(appinfo.appPkg, context.packageManager)
        }
        OLog.i("列表项", pkglist.toString())
        return pkglist
    }

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
                OptPage()
            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var app: XPlan private set
    }
}




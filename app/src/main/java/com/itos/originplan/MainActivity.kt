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
import android.os.Bundle
import android.text.util.Linkify
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
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
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

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
) {
}

// TODO 拆分About页面
class MainActivity : AppCompatActivity() {
    private val context: Context = this
    //var userService: IUserService? = null

    //    val pkglist: List<AppInfo> = listOf(
//        AppInfo("mt", "bin.mt.plus.canary"),
//        AppInfo("origin read", "com.vivo.newsreader"),
//        AppInfo("douyin", "com.ss.android.ugc.aweme"),
//        AppInfo("zhuti", "com.bbk.theme"),
//        AppInfo("kuan", "com.coolapk.market")
//        )
    private val pkglist = mutableListOf<AppInfo>()
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
            val inputStream = resources.openRawResource(R.raw.pkglist)
            val reader = BufferedReader(InputStreamReader(inputStream))

            // 逐行读取文件内容
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val packageName = line!!.trim()
                // 创建 AppInfo 对象，并添加到列表
                val appInfo = AppInfo(appName = "", appPkg = packageName)
                pkglist.add(appInfo)
            }
        } catch (e: Exception) {
            // 处理异常，例如文件不存在等情况
            e.printStackTrace()
        }

        Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)
        checkShizuku()
        Shizuku.addBinderReceivedListenerSticky(BINDER_RECEVIED_LISTENER)
        Shizuku.addBinderDeadListener(BINDER_DEAD_LISTENER)

        // TODO OTA和多线程，用的kt的协程
        update()

//        Shizuku.bindUserService(userServiceArgs, userServiceConnection)
    }

    fun update() {
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
                OLog.i(
                    "更新",
                    update + "\n" + version + "\n" + url + "\n" + version_name + "\n" + log
                )
                if (BuildConfig.VERSION_CODE < version.toInt()) {
                    OLog.i("更新", "有新版本")
                    MaterialAlertDialogBuilder(context)
                        .setTitle("有新版本")
                        .setMessage("最新版本：$version_name($version)\n\n更新日志：\n$log")
                        .setPositiveButton("前往更新") { dialog, which ->
                            // 点击支付宝按钮后的操作
                            openLink(url)
                            finish()
                        }
                        .setCancelable(false)
                        .show()
                }

            }
        }
    }

    private fun checkShizuku() {
        var b = true
        var c = false
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
                Toast.LENGTH_SHORT
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

    fun gift() {
        val show_text = """
                您可以通过微信或支付宝来捐赠

                如果您有条件的话,希望可以捐赠一点
                不求多少,支持一下我们,非常感谢

                本工具永久免费!!!
                """.trimIndent()
        MaterialAlertDialogBuilder(this)
            .setTitle("捐赠")
            .setMessage(show_text)
            .setPositiveButton("支付宝") { dialog, which ->
                // 点击支付宝按钮后的操作
                dialog.dismiss()
                showImageDialog("zfb.jpg")
            }
            .setNegativeButton("微信") { dialog, which ->
                // 点击微信按钮后的操作
                dialog.dismiss()
                showImageDialog("wx.png")
            }
            .show()
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

    fun getAppIconByPackageName(packageName: String): Drawable? {
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
        OriginPlanTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                About()
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                text = if (!appInfo.isExist) "Unknow" else if (isDisabled.value) "Disable" else "Enable",
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
            colors = colors
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
        val context = LocalContext.current

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
                label = "BilibBili（开发者）",
                onClick = {
                    openLink("https://space.bilibili.com/329223542")
                }
            ),
            OriginCardItem(
                icon = ImageVector.Companion.vectorResource(R.drawable.ic_bilibili),
                label = "BilibBili（合作伙伴）",
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun About() {
        Column {
            // TopAppBar
            TopAppBar(title = { Text(text = "关于") })
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
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
        val appList = remember { generateAppList(context) }
        var expanded by remember { mutableStateOf(false) }
        Column {
            // TopAppBar
            TopAppBar(

                title = { Text(text = "原·初") },

//                actions = {
//                    IconButton(
//                        onClick = { expanded = true }
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.List,
//                            // tint = Color.White,
//                            contentDescription = "菜单"
//                        )
//                    }
//                    DropdownMenu(
//                        expanded = expanded,
//                        onDismissRequest = { expanded = false },
//                        modifier = Modifier.padding(8.dp)
//                    ) {
//                        // 添加菜单项
//
//
//                        DropdownMenuItem(
//                            text = { Text(text = "开发者酷安") },
//                            onClick = {
//                                expanded =
//                                    false; show_author()
//                            },
//                            leadingIcon = {
//                                Icon(
//                                    imageVector = ImageVector.Companion.vectorResource(R.drawable.ic_outline_coolapk),
//                                    contentDescription = "Coolapk"
//                                )
//                            }
//                        )
//                        DropdownMenuItem(
//                            text = { Text(text = "捐赠") },
//                            onClick = {
//                                expanded =
//                                    false; gift()
//                            },
//                            leadingIcon = {
//                                Icon(
//                                    imageVector = ImageVector.Companion.vectorResource(R.drawable.ic_outline_giftcard),
//                                    contentDescription = "money"
//                                )
//                            }
//                        )
//                        DropdownMenuItem(
//                            text = { Text(text = "GitHub") },
////                                colors = MenuDefaults.itemColors(textColor = Color.White),
//                            onClick = {
//                                expanded =
//                                    false; openLink("https://github.com/ItosEO/OriginPlan")
//                            },
//                            leadingIcon = {
//                                Icon(
//                                    imageVector = ImageVector.Companion.vectorResource(R.drawable.ic_outline_code),
//                                    contentDescription = "GitHub"
//                                )
//                            }
//                        )
//                        DropdownMenuItem(
//                            text = { Text(text = "许可证") },
//                            onClick = { expanded = false; showLicenses() },
//                            leadingIcon = {
//                                Icon(
//                                    imageVector = ImageVector.Companion.vectorResource(R.drawable.ic_outline_lisence),
//                                    contentDescription = "GitHub"
//                                )
//                            }
//                        )
//
//                        // 添加更多菜单项...
//                    }
//                }
            )

            // AppList
            AppList(appList = appList)
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppListScreen(context: Context) {
        val navController = rememberNavController()

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

            NavHost(
                navController = navController,
                startDestination = "2"
            ) {
                composable("2") { Details() }
                composable("3") { About() }
                composable("1") { About() }

                // 添加其他页面的 composable 函数，类似上面的示例
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




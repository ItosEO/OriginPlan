package com.itos.xplan.utils

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itos.xplan.XPlan.Companion.app
import java.io.IOException


object OUI {

    fun check_secure_premission() {
        try {
            Settings.Secure.putString(app.contentResolver, "xplan", "test")
            OData.is_have_premissipn = true
        } catch (e: Exception) {
            OLog.e("写入安全设置权限异常", e)
            OLog.i("写入安全设置权限异常", "$app.b $app.c")
            if (app.isShizukuStart && app.isShizukuAuthorized) {
                val temp = app.ShizukuExec("pm grant com.itos.xplan android.permission.WRITE_SECURE_SETTINGS".toByteArray())
                if (temp == "") {
                    OData.is_have_premissipn = true
                } else {
                    OData.is_have_premissipn = false
                    OLog.i("设置 写入安全设置权限异常", temp!!)
                }
            } else {
                OShizuku.checkShizuku()
            }

        }
    }

    fun openLink(url: String) {
        app.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun showImageDialog(imageName: String) {
        val builder: AlertDialog.Builder = MaterialAlertDialogBuilder(app)

        // 创建一个 ImageView 并添加到对话框中
        val imageView = ImageView(app)
        try {
            val `is` = app.assets.open(imageName)
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
}
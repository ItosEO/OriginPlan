package com.itos.xplan.utils

import android.R.attr.data
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.Settings
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startForegroundService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itos.xplan.XPlan.Companion.app
import rikka.shizuku.Shizuku
import java.io.IOException


object OUI {

    fun check_secure_premission() :Boolean{
        try {
            Settings.Global.putInt(app.contentResolver, "test", 1)
            return true
        } catch (e: Exception) {
            OShizuku.checkShizuku()
            if (app.b && app.c) {
                val p: Process = Shizuku.newProcess(arrayOf("sh"), null, null)
                val out = p.outputStream
                out.write(("pm grant com.itos.xplan android.permission.WRITE_SECURE_SETTINGS" + "\nexit\n").toByteArray())
                out.flush()
                out.close()
            } else {
                OShizuku.checkShizuku()
            }
            return false
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
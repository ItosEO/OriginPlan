package com.itos.originplan.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itos.originplan.MainActivity
import java.io.IOException

 fun showImageDialog(imageName: String,context: Context) {
    val builder: AlertDialog.Builder = MaterialAlertDialogBuilder(context)

    // 创建一个 ImageView 并添加到对话框中
    val imageView = ImageView(context)
    try {
        val `is` = (context as? MainActivity)?.assets?.open(imageName)
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
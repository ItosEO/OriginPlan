package com.itos.originplan.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.getSystemService
import com.itos.originplan.MainActivity.Companion.app
import com.itos.originplan.R


object OUI {
    fun showToast(text: CharSequence, isLengthLong: Boolean = false) = Toast.makeText(
        app, text,
        if (isLengthLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()


    fun startActivity(action: String = Intent.ACTION_VIEW, uri: String): Boolean = runCatching {
        app.startActivity(
            Intent(action, Uri.parse(uri)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        true
    }.getOrDefault(false)

    fun openLink(url: String): Boolean = startActivity(uri = url)

    fun copyText(text: String) = app.getSystemService<ClipboardManager>()
        ?.setPrimaryClip(ClipData.newPlainText(app.getString(R.string.app_name), text))

    fun pasteText(): String? = app.getSystemService<ClipboardManager>()
        ?.primaryClip?.getItemAt(0)?.text?.toString()
}
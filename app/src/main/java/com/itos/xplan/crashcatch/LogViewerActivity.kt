package com.itos.xplan.crashcatch

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.itos.xplan.R


class LogViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_viewer)
        val textview: TextView = findViewById(R.id.text_log)
        val log_summary = intent.getStringExtra("log_summary")
        if (!log_summary.isNullOrEmpty()) {
            textview.text = log_summary
        }
        val cm: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("Crash", log_summary)
        cm.setPrimaryClip(mClipData)
    }
}
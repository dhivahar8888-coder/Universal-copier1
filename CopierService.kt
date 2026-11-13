package com.example.universalcopier

import android.accessibilityservice.AccessibilityService
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class CopierService : AccessibilityService() {
    private lateinit var wm: WindowManager
    private var floating: Button? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val button = Button(this).apply {
            text = "COPY"
            textSize = 12f
            setOnClickListener { showTextChoices() }
        }
        floating = button
        val type = if (Build.VERSION.SDK_INT >= 26) WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
                   else WindowManager.LayoutParams.TYPE_PHONE
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.END; x = 12; y = 220 }
        wm.addView(button, params)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    private fun showTextChoices() {
        val lines = mutableListOf<String>()
        fun walk(node: AccessibilityNodeInfo?) {
            if (node == null || lines.size >= 100) return
            val t = node.text?.toString()?.trim().orEmpty()
            if (t.isNotEmpty() && t !in lines) lines.add(t)
            for (i in 0 until node.childCount) walk(node.getChild(i))
        }
        walk(rootInActiveWindow)
        if (lines.isEmpty()) {
            AlertDialog.Builder(this)
                .setMessage("This app does not expose readable text to accessibility. Try selecting/copying text normally in that app.")
                .setPositiveButton("OK", null).show()
            return
        }
        val scroll = ScrollView(this)
        val list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(20, 12, 20, 12) }
        lines.forEachIndexed { index, line ->
            val item = TextView(this).apply {
                text = line.take(220)
                textSize = 15f
                setPadding(8, 18, 8, 18)
                setOnClickListener { showEditor(line) }
            }
            list.addView(item)
        }
        scroll.addView(list)
        AlertDialog.Builder(this).setTitle("Choose text").setView(scroll)
            .setNegativeButton("Cancel", null).show()
    }

    private fun showEditor(source: String) {
        val input = EditText(this).apply {
            setText(source)
            setSelection(0, text.length)
            minLines = 3
            maxLines = 10
            hint = "Select the exact text to copy"
        }
        AlertDialog.Builder(this).setTitle("Select exact text").setView(input)
            .setPositiveButton("Copy selected") { _, _ ->
                val start = input.selectionStart.coerceAtLeast(0)
                val end = input.selectionEnd.coerceAtLeast(0)
                if (start != end) {
                    val selected = input.text.substring(minOf(start, end), maxOf(start, end))
                    val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cb.setPrimaryClip(ClipData.newPlainText("copied text", selected))
                }
            }.setNegativeButton("Cancel", null).show()
    }

    override fun onDestroy() {
        floating?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        floating = null
        super.onDestroy()
    }
}

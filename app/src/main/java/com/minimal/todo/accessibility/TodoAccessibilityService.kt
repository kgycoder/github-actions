package com.minimal.todo.accessibility

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.EditText
import android.widget.ImageButton
import com.minimal.todo.R
import com.minimal.todo.TodoApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Accessibility service that exposes a system Accessibility Button.
 * Tapping it opens a lightweight "quick add todo" overlay drawn on top
 * of whatever screen the user currently has open, using
 * TYPE_ACCESSIBILITY_OVERLAY (no SYSTEM_ALERT_WINDOW permission needed).
 */
class TodoAccessibilityService : AccessibilityService() {

    private var overlayView: View? = null
    private val windowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onServiceConnected() {
        super.onServiceConnected()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            accessibilityButtonController.registerAccessibilityButtonCallback(
                object : AccessibilityButtonController.AccessibilityButtonCallback() {
                    override fun onClicked(controller: AccessibilityButtonController) {
                        toggleOverlay()
                    }
                }
            )
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No-op: this service only reacts to the accessibility button.
    }

    override fun onInterrupt() {
        removeOverlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        removeOverlay()
    }

    private fun toggleOverlay() {
        if (overlayView != null) removeOverlay() else showOverlay()
    }

    private fun showOverlay() {
        if (overlayView != null) return

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.overlay_add_todo, null)

        val editText = view.findViewById<EditText>(R.id.overlay_input)
        val saveButton = view.findViewById<ImageButton>(R.id.overlay_save)
        val cancelButton = view.findViewById<ImageButton>(R.id.overlay_cancel)
        val background = view.findViewById<View>(R.id.overlay_background)

        background.setOnClickListener { removeOverlay() }
        cancelButton.setOnClickListener { removeOverlay() }
        saveButton.setOnClickListener {
            val text = editText.text?.toString()?.trim().orEmpty()
            if (text.isNotEmpty()) {
                val app = applicationContext as TodoApplication
                scope.launch(Dispatchers.IO) { app.repository.addTodo(text) }
            }
            removeOverlay()
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        windowManager.addView(view, params)
        overlayView = view
    }

    private fun removeOverlay() {
        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
}
